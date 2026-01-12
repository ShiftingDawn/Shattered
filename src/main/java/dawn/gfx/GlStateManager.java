package dawn.gfx;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.glUseProgram;

public final class GlStateManager {

	private static int SHADER = 0;
	private static int TEXTURE = 0;
	private static boolean BLEND = false;
	private static int BLEND_FUNC_SRC = 0;
	private static int BLEND_FUNC_DST = 0;

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

	public static void blend(final boolean blend, final int src, final int dst) {
		if (blend != GlStateManager.BLEND) {
			if (blend) {
				glEnable(GL_BLEND);
			} else {
				glDisable(GL_BLEND);
			}
			GlStateManager.BLEND = blend;
		}
		if (src != GlStateManager.BLEND_FUNC_SRC || dst != GlStateManager.BLEND_FUNC_DST) {
			GlStateManager.BLEND_FUNC_SRC = src;
			GlStateManager.BLEND_FUNC_DST = dst;
			glBlendFunc(src, dst);
		}
	}

	public static void blendSimple() {
		GlStateManager.blend(true, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void blendNone() {
		GlStateManager.blend(false, 0, 0);
	}

	private GlStateManager() {
	}
}
