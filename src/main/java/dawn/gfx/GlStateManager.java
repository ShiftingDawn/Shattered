package dawn.gfx;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.glUseProgram;

public final class GlStateManager {

	private static int SHADER = 0;
	private static int TEXTURE = 0;

	public static void bindShader(final int shader) {
		if (shader != GlStateManager.SHADER) {
			glUseProgram(shader);
			GlStateManager.SHADER = shader;
		}
	}

	public static void bindTexture(final int texture) {
		if (texture != GlStateManager.TEXTURE) {
			glBindTexture(GL_TEXTURE_2D, texture);
			GlStateManager.TEXTURE = texture;
		}
	}

	private GlStateManager() {
	}
}
