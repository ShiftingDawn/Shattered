package dawn.asset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentHashMap;
import dawn.Dawn;
import dawn.lib.Util;
import dawn.registry.Identifier;
import dawn.registry.Registries;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
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
	private final AssetManager assets;

	void init() {
		TextureManager.LOGGER.info("Reloading textures");
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
			final ByteBuffer buffer = this.assets.getResources().getBuffer(texture.getRegistryKey(), "texture", "png");
			if (buffer == null) {
				//TODO implement missing texture
				throw new FileNotFoundException();
			}
			try (MemoryStack stack = stackPush()) {
				TextureManager.LOGGER.debug("\t\tAllocating texture data");
				final IntBuffer widthPtr = stack.mallocInt(1);
				final IntBuffer heightPtr = stack.mallocInt(1);
				final IntBuffer channelPtr = stack.mallocInt(1);
				TextureManager.LOGGER.debug("\t\tLoading texture data");
				final ByteBuffer image = stbi_load_from_memory(buffer, widthPtr, heightPtr, channelPtr, 0);
				if (image == null) {
					//TODO better exception
					throw new RuntimeException("Could not load image: " + stbi_failure_reason());
				}
				final int width = widthPtr.get(0);
				final int height = heightPtr.get(0);
				final int glFormat = channelPtr.get(0) == 4 ? GL_RGBA : GL_RGB;
				TextureManager.LOGGER.debug("\t\tLoading GL data");
				final int textureId = this.makeTextureId();
				glTexImage2D(GL_TEXTURE_2D, 0, glFormat, width, height, 0, glFormat, GL_UNSIGNED_BYTE, image);
				stbi_image_free(image);
				final Texture result = new Texture(textureId, width, height);
				this.mapping.put(texture.getRegistryKey(), result);
				TextureManager.LOGGER.debug("\t\tDone");
			}
		} catch (final IOException e) {
			//TODO implement missing texture
			throw new RuntimeException(e);
		}
	}

	private void unloadTexture(final TextureAsset texture) {
		final int textureId = this.getTextureId(texture);
		if (textureId > 0) {
			TextureManager.LOGGER.debug("\tUnloading texture {} ({})", texture.getRegistryKey(), textureId);
			glDeleteTextures(textureId);
			this.mapping.remove(texture.getRegistryKey());
			TextureManager.LOGGER.debug("\t\tDone");
		}
	}

	public int getTextureId(final Identifier texture) {
		final Texture tex = this.mapping.get(texture);
		return tex == null ? -1 : tex.id();
	}

	public int getTextureId(final TextureAsset texture) {
		return this.getTextureId(texture.getRegistryKey());
	}

	private int makeTextureId() {
		return Util.make(glGenTextures(), id -> {
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		});
	}
}
