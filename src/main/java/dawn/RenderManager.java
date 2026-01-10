package dawn;

import dawn.asset.AssetResolver;
import dawn.event.EventBus;
import dawn.gfx.Display;
import dawn.gfx.DisplayResizedEvent;
import dawn.gfx.MatrixUtils;
import dawn.gfx.Shader;
import dawn.gfx.ShaderProps;
import dawn.gfx.Tessellator;
import dawn.lib.Color;
import lombok.Getter;
import static org.lwjgl.opengl.GL11.glViewport;

public final class RenderManager {

	private final Shader shader;
	private final @Getter Tessellator tessellator;

	RenderManager(AssetResolver assets) {
		Display.activate();
		this.shader = new Shader(assets, "root", "outColor");
		this.shader.bind();
		this.tessellator = new Tessellator(this.shader);
		EventBus.register(DisplayResizedEvent.class, _ -> this.resetShader());
		this.resetShader();
	}

	void render() {
		//TODO render gui
		tessellator.start().set(0, 0, Display.getWidth(), Display.getHeight() / 2).draw(Color.MAGENTA).end();
	}

	private void resetShader() {
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "projectionMatrix"), false, MatrixUtils.ortho());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "modelViewMatrix"), false, MatrixUtils.identity());
	}
}
