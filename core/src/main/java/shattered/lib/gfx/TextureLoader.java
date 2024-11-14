package shattered.lib.gfx;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryStack;
import shattered.core.ResourceLoader;
import shattered.lib.resource.InvalidTextureException;
import shattered.lib.util.Utils;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_LINEAR;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public final class TextureLoader {

	public static Texture loadTexture(final String path) throws IOException {
		return TextureLoader.makeTexture(ResourceLoader.getResourceAsBuffer(path));
	}

	private static Texture makeTexture(final ByteBuffer imageBuffer) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer w = stack.mallocInt(1);
			final IntBuffer h = stack.mallocInt(1);
			final IntBuffer c = stack.mallocInt(1);

			final ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, c, 0);
			if (image == null) {
				//TODO better exception
				throw new InvalidTextureException("Could not load image: " + stbi_failure_reason());
			}
			final int width = w.get(0);
			final int height = h.get(0);
			final int glFormat = c.get(0) == 4 ? GL_RGBA : GL_RGB;
			final int textureId = TextureLoader.generateAndConfigureGl();

			glTexImage2D(GL_TEXTURE_2D, 0, glFormat, width, height, 0, glFormat, GL_UNSIGNED_BYTE, image);
			stbi_image_free(image);

			return new Texture(textureId, width, height);
		}
	}

	private static int generateAndConfigureGl() {
		return Utils.make(glGenTextures(), id -> {
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		});
	}
}
