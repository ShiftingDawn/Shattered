package dawn.core.gfx;

import dawn.gfx.GlStateManager;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL20.glUseProgram;

public final class GlStateManagerImpl implements GlStateManager {

	private int shader = 0;
	private int texture = 0;
	private boolean blend = false;
	private int blendFuncSrc = 0;
	private int blendFuncDst = 0;

	@Override
	public void bindShader(final int shader) {
		if (shader != this.shader) {
			glUseProgram(shader);
			this.shader = shader;
		}
	}

	@Override
	public void bindTexture(final int texture) {
		if (texture != this.texture) {
			glBindTexture(GL_TEXTURE_2D, texture);
			this.texture = texture;
		}
	}

	@Override
	public void blend(final boolean blend, final int src, final int dst) {
		if (blend != this.blend) {
			if (blend) {
				glEnable(GL_BLEND);
			} else {
				glDisable(GL_BLEND);
			}
			this.blend = blend;
		}
		if (src != this.blendFuncSrc || dst != this.blendFuncDst) {
			this.blendFuncSrc = src;
			this.blendFuncDst = dst;
			glBlendFunc(src, dst);
		}
	}

	@Override
	public void blendSimple() {
		this.blend(true, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void blendNone() {
		this.blend(false, 0, 0);
	}

	@Override
	public void textureFilterMin(final int value) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, value);
	}

	@Override
	public void textureFilterMag(final int value) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, value);
	}

	@Override
	public void textureFilterAll(final int value) {
		this.textureFilterMin(value);
		this.textureFilterMag(value);
	}

	@Override
	public void textureFilterHard() {
		this.textureFilterAll(GL_NEAREST);
	}

	@Override
	public void textureFilterSmooth() {
		this.textureFilterAll(GL_LINEAR);
	}
}
