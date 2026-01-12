package dawn.asset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentHashMap;
import dawn.Dawn;
import dawn.lib.ExitException;
import dawn.lib.Util;
import dawn.registry.Identifier;
import dawn.registry.Registries;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
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

	private static final Logger LOGGER = Dawn.getLogger("Textures");
	private final ConcurrentHashMap<Identifier, Texture> mapping = new ConcurrentHashMap<>();
	private final TextureAsset missingTextureAsset = new TextureAsset.Default(Identifier.of("missing"));
	private final AssetManager assets;

	void init() {
		TextureManager.LOGGER.info("Reloading textures");
		this.loadMissingTexture();
		for (final TextureAsset texture : Registries.TEXTURES) {
			this.unloadTexture(texture);
			this.loadTexture(texture);
		}
	}

	private void loadTexture(final TextureAsset texture) {
		TextureManager.LOGGER.debug("\tLoading texture {}", texture.getRegistryKey());
		if (this.mapping.containsKey(texture.getRegistryKey())) {
			TextureManager.LOGGER.error("\tTrying to load duplicate texture {}. This will most likely result in a memory leak.", texture.getRegistryKey());
		}
		try {
			TextureManager.LOGGER.debug("\t\tReading texture file");
			final String path = this.assets.getResources().makePath(texture.getRegistryKey(), "texture", "png");
			final ByteBuffer buffer = this.assets.getResources().getBuffer(path);
			if (buffer == null) {
				throw new FileNotFoundException("Texture file does not exist. Expected path: " + path);
			}
			final Texture result = TextureManager.makeTextureFromData(buffer, true);
			this.mapping.put(texture.getRegistryKey(), result);
			TextureManager.LOGGER.debug("\t\tDone");
		} catch (final IOException e) {
			TextureManager.LOGGER.error("Could not load texture {}. Using missing texture instead.", texture.getRegistryKey());
			TextureManager.LOGGER.error(e);
			this.mapping.put(texture.getRegistryKey(), this.mapping.get(this.missingTextureAsset.getRegistryKey()));
		}
	}

	public static Texture makeTextureFromData(final ByteBuffer textureData, final boolean log) {
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
			return new Texture(textureId, width, height);
		}
	}

	private void unloadTexture(final TextureAsset texture) {
		final Texture tex = this.mapping.get(texture.getRegistryKey());
		if (tex != null) {
			TextureManager.LOGGER.debug("\tUnloading texture {} ({})", texture.getRegistryKey(), tex.id());
			glDeleteTextures(tex.id());
			this.mapping.remove(texture.getRegistryKey());
			TextureManager.LOGGER.debug("\t\tDone");
		}
	}

	public Texture getTexture(final Identifier texture) {
		return this.mapping.computeIfAbsent(texture, _ -> this.mapping.get(this.missingTextureAsset.getRegistryKey()));
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
		this.unloadTexture(this.missingTextureAsset);
		TextureManager.LOGGER.debug("\tGenerating missing texture");
		if (this.mapping.containsKey(this.missingTextureAsset.getRegistryKey())) {
			TextureManager.LOGGER.error("\tTrying to load duplicate missing texture. This will most likely result in a memory leak.");
		}
		final byte[] data = TextureManager.generateMissingTextureData();
		final ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		final Texture result = TextureManager.makeTextureFromData(buffer, true);
		this.mapping.put(this.missingTextureAsset.getRegistryKey(), result);
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
