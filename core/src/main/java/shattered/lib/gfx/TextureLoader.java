package shattered.lib.gfx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import shattered.core.ResourceLoader;
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

	@Getter
	private final int id;

	public TextureLoader(final String path) throws IOException {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer w = stack.mallocInt(1);
			final IntBuffer h = stack.mallocInt(1);
			final IntBuffer c = stack.mallocInt(1);
			
			final ByteBuffer imageBuffer;
			try (InputStream stream = ResourceLoader.getResourceAsStream(path)) {
				final byte[] bytes = stream.readAllBytes();
				imageBuffer = BufferUtils.createByteBuffer(bytes.length);
				imageBuffer.put(bytes);
			}
			final ByteBuffer image = stbi_load_from_memory(imageBuffer.flip(), w, h, c, 0);
			if (image == null) {
				//TODO better exception
				throw new IllegalStateException("Could not load image '%s', reason: %s".formatted(path, stbi_failure_reason()));
			}
			final int width = w.get(0);
			final int height = h.get(0);
			final int channels = c.get(0);

			this.id = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, this.id);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

			final int format;
			if (channels == 4) {
				//				glEnable(GL_BLEND);
				//				glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
				//				final int stride = width * 4;
				//				for (int y = 0; y < height; y++) {
				//					for (int x = 0; x < width; x++) {
				//						final int i = y * stride + x * 4;
				//
				//						final float alpha = (image.get(i + 3) & 0xFF) / 255.0f;
				//						image.put(i + 0, (byte) Math.round(((image.get(i + 0) & 0xFF) * alpha)));
				//						image.put(i + 1, (byte) Math.round(((image.get(i + 1) & 0xFF) * alpha)));
				//						image.put(i + 2, (byte) Math.round(((image.get(i + 2) & 0xFF) * alpha)));
				//					}
				//				}
				format = GL_RGBA;
			} else {
				//				if ((width & 3) != 0) {
				//					glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
				//				}
				format = GL_RGB;
			}

			glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, image);

			stbi_image_free(image);
		}
	}
}
