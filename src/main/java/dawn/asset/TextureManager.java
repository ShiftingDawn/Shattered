package dawn.asset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import dawn.Dawn;
import dawn.gfx.Display;
import dawn.lib.ExitException;
import dawn.lib.Tuple;
import dawn.lib.Util;
import dawn.registry.Identifier;
import dawn.registry.Registries;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class TextureManager {

	public static final Identifier ATLAS = Identifier.of("atlas");
	public static final Identifier MISSING = Identifier.of("missing");
	private static final Logger LOGGER = Dawn.getLogger("Textures");
	private final List<TextureAsset> loadedTextures = new CopyOnWriteArrayList<>();
	private final ConcurrentHashMap<Identifier, Texture> textures = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Identifier, TextureAtlas> atlasses = new ConcurrentHashMap<>();
	private final AssetManager assets;

	void init() {
		TextureManager.LOGGER.info("Reloading textures");
		this.atlasses.clear();
		for (final Identifier texture : this.textures.keySet()) {
			this.unloadTexture(texture);
		}
		this.loadedTextures.clear();
		this.loadMissingTexture();
		for (final TextureAsset texture : Registries.TEXTURES) {
			this.loadTexture(texture);
		}
		this.stitch();
	}

	private void loadTexture(final TextureAsset texture) {
		TextureManager.LOGGER.debug("\tLoading texture {}", texture.getRegistryKey());
		if (this.loadedTextures.contains(texture)) {
			TextureManager.LOGGER.error("\tTrying to load duplicate texture {}, skipping.", texture.getRegistryKey());
			return;
		}
		try {
			TextureManager.LOGGER.debug("\t\tReading texture file");
			final String path = this.assets.getResources().makePath(texture.getRegistryKey(), "texture", "png");
			final URL url = this.assets.getResources().getResource(path);
			if (url == null) {
				throw new FileNotFoundException("Texture file does not exist. Expected path: " + path);
			}
			this.loadedTextures.add(texture);
			TextureManager.LOGGER.debug("\t\tDone");
		} catch (final IOException e) {
			TextureManager.LOGGER.error("Could not load texture {}. Using missing texture instead.", texture.getRegistryKey());
			TextureManager.LOGGER.error(e);
		}
	}

	public static Texture makeTextureFromData(final TextureAsset assetData, final ByteBuffer textureData, final boolean log) {
		try (MemoryStack stack = stackPush()) {
			if (log) {
				TextureManager.LOGGER.debug("\t\tAllocating texture data");
			}
			final IntBuffer widthPtr = stack.mallocInt(1);
			final IntBuffer heightPtr = stack.mallocInt(1);
			final IntBuffer channelPtr = stack.mallocInt(1);
			if (log) {
				TextureManager.LOGGER.debug("\t\tLoading texture data");
			}
			final ByteBuffer image = stbi_load_from_memory(textureData, widthPtr, heightPtr, channelPtr, 0);
			if (image == null) {
				TextureManager.LOGGER.fatal("Could not load image: {}", stbi_failure_reason());
				throw new ExitException();
			}
			final int width = widthPtr.get(0);
			final int height = heightPtr.get(0);
			final int glFormat = channelPtr.get(0) == 4 ? GL_RGBA : GL_RGB;
			if (log) {
				TextureManager.LOGGER.debug("\t\tLoading GL data");
			}
			final int textureId = TextureManager.makeTextureId();
			glTexImage2D(GL_TEXTURE_2D, 0, glFormat, width, height, 0, glFormat, GL_UNSIGNED_BYTE, image);
			stbi_image_free(image);
			return new AbsoluteTexture(assetData, textureId, width, height, new int[] { 0, 0, width, height });
		}
	}

	private record StitchEntry(ByteBuffer data, int w, int h) {
	}

	private void stitch() {
		final Map<Identifier, Tuple<TextureAsset, StitchEntry>> entries = new HashMap<>();
		for (final TextureAsset texture : this.loadedTextures) {
			try {
				final String path = this.assets.getResources().makePath(texture.getRegistryKey(), "texture", "png");
				final ByteBuffer buffer = this.assets.getResources().getBuffer(path);
				if (buffer == null) {
					continue;
				}
				try (MemoryStack stack = stackPush()) {
					final IntBuffer widthPtr = stack.mallocInt(1);
					final IntBuffer heightPtr = stack.mallocInt(1);
					final IntBuffer channelPtr = stack.mallocInt(1);
					final ByteBuffer image = stbi_load_from_memory(buffer, widthPtr, heightPtr, channelPtr, 4);
					assert image != null;
					entries.put(texture.getRegistryKey(), new Tuple<>(texture, new StitchEntry(image, widthPtr.get(), heightPtr.get())));
				}
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		final List<Identifier> sorted = entries.entrySet().stream()
			.sorted((a, b) -> Long.compare((long) b.getValue().b().w() * b.getValue().b().h(), (long) a.getValue().b().w() * a.getValue().b().h()))
			.map(Map.Entry::getKey)
			.toList();
		int atlasWidth = 256;
		int atlasHeight = 256;
		TextureManager.LOGGER.debug("Stitching atlas with size {}x{}", atlasWidth, atlasHeight);
		while (true) {
			try (ImagePacker packer = new ImagePacker(atlasWidth, atlasHeight)) {
				for (final Identifier texture : sorted) {
					final Tuple<TextureAsset, StitchEntry> entry = entries.get(texture);
					if (entry != null) {
						packer.addImage(texture, entry.b().data, entry.b().w, entry.b().h);
					}
				}
				final InMemoryPngWriter writer = new InMemoryPngWriter();
				STBImageWrite.stbi_write_png_to_func(writer, Display.getWindow(), packer.getAtlasWidth(), packer.getAtlasHeight(), 4, packer.getBuffer(), packer.getAtlasWidth() * 4);
				this.assets.dumpAsset("atlas/%s.png".formatted(TextureManager.ATLAS.toPathSafeString()), writer.getData());
				writer.getData().position(0);
				final Texture texture = TextureManager.makeTextureFromData(new TextureAsset.Default(TextureManager.ATLAS), writer.getData(), true);
				final TextureAtlas atlas = new TextureAtlas(texture, packer.getElements(), Util.make(new HashMap<>(), map -> {
					entries.forEach((k, v) -> map.put(k, v.a()));
				}));
				this.textures.put(TextureManager.ATLAS, atlas);
				this.atlasses.put(TextureManager.ATLAS, atlas);
				entries.keySet().forEach(textureEntry -> this.textures.put(textureEntry, atlas));
				writer.free();
				break;
			} catch (final RuntimeException e) {
				if (!"ATLAS_TOO_SMALL".equals(e.getMessage())) {
					throw e;
				}
			}
			if (atlasWidth == atlasHeight) {
				atlasWidth *= 2;
			} else {
				atlasHeight *= 2;
			}
			TextureManager.LOGGER.debug("Atlas too small, trying {}x{}", atlasWidth, atlasHeight);
		}
		for (final Tuple<TextureAsset, StitchEntry> entry : entries.values()) {
			stbi_image_free(entry.b().data());
		}
	}

	private void unloadTexture(final Identifier texture) {
		final Texture tex = this.textures.get(texture);
		if (tex != null) {
			TextureManager.LOGGER.debug("\tUnloading texture {} ({})", texture, tex.id());
			glDeleteTextures(tex.id());
			this.textures.remove(texture);
			TextureManager.LOGGER.debug("\t\tDone");
		}
	}

	public Texture getTexture(final Identifier texture) {
		Texture result = this.textures.get(texture);
		if (result instanceof final TextureAtlas atlas && !atlas.asset().getRegistryKey().equals(texture)) {
			result = atlas.getTexture(texture);
		}
		if (result == null) {
			result = this.textures.get(TextureManager.MISSING);
			this.textures.put(texture, result);
		}
		return result;
	}

	public Texture getTexture(final TextureAsset texture) {
		return this.getTexture(texture.getRegistryKey());
	}

	private static int makeTextureId() {
		return Util.make(glGenTextures(), id -> {
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		});
	}

	private void loadMissingTexture() {
		this.unloadTexture(TextureManager.MISSING);
		TextureManager.LOGGER.debug("\tGenerating missing texture");
		if (this.textures.containsKey(TextureManager.MISSING)) {
			TextureManager.LOGGER.error("\tTrying to load duplicate missing texture. This will most likely result in a memory leak.");
		}
		final byte[] data = TextureManager.generateMissingTextureData();
		final ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		final Texture result = TextureManager.makeTextureFromData(new TextureAsset.Default(TextureManager.MISSING), buffer, true);
		this.textures.put(TextureManager.MISSING, result);
		TextureManager.LOGGER.debug("\t\tDone");
	}

	private static byte[] generateMissingTextureData() {
		return new byte[] {
			-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 2, 0, 0, 0, 2, 8, 2, 0, 0, 0, -3, -44, -102, 115, 0, 0, 0, 1,
			115, 82, 71, 66, 1, -39, -55, 44, 127, 0, 0, 0, 4, 103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0,
			122, 38, 0, 0, -128, -124, 0, 0, -6, 0, 0, 0, -128, -24, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0, 23, 112, -100, -70, 81,
			60, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 46, 35, 0, 0, 46, 35, 1, 120, -91, 63, 118, 0, 0, 0, 16, 73, 68, 65, 84, 8, -41, 99, 96, 96, 96,
			-8, 15, -123, 12, 12, 0, 27, -14, 3, -3, 10, 22, -84, 109, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
		};
	}
}
