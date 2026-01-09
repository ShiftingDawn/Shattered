package dawn;

import dawn.asset.AssetResolver;
import dawn.gfx.Display;
import dawn.gfx.MatrixUtils;
import dawn.gfx.Shader;
import dawn.gfx.ShaderProps;
import static org.lwjgl.opengl.GL11.glViewport;

public final class RenderManager {

	private final Shader shader;

	RenderManager(AssetResolver assets) {
		Display.activate();
		this.shader = new Shader(assets, "root", "outColor");
		this.shader.bind();
		resetShader();
	}

	void render() {
		//TODO render gui
	}

	private void resetShader() {
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "projectionMatrix"), false, MatrixUtils.ortho());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "modelViewMatrix"), false, MatrixUtils.ortho());
	}
}
