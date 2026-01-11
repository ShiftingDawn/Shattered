package dawn;

import dawn.asset.AssetManager;
import dawn.asset.ResourceResolver;
import dawn.event.EventBus;
import dawn.gfx.Display;
import dawn.gfx.DisplayResizedEvent;
import dawn.gfx.MatrixUtils;
import dawn.gfx.Shader;
import dawn.gfx.ShaderProps;
import dawn.gfx.Tessellator;
import dawn.init.Textures;
import dawn.lib.Color;
import lombok.Getter;
import static org.lwjgl.opengl.GL11.glViewport;

public final class RenderManager {

	private final Shader shader;
	private final @Getter Tessellator tessellator;

	RenderManager(final ResourceResolver resources, final AssetManager assets) {
		Display.activate();
		this.shader = new Shader(resources, "root", "outColor");
		this.shader.bind();
		this.tessellator = new Tessellator(this.shader, assets.getTextures());
		EventBus.register(DisplayResizedEvent.class, _ -> this.resetShader());
		this.resetShader();
	}

	void render() {
		//TODO render gui
		this.tessellator.start().set(0, 0, Display.getWidth(), Display.getHeight() / 2).draw(Textures.ARGON, Color.YELLOW).end();
	}

	private void resetShader() {
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "projectionMatrix"), false, MatrixUtils.ortho());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "modelViewMatrix"), false, MatrixUtils.identity());
	}
}
