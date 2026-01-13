package dawn;

import dawn.asset.AssetManager;
import dawn.event.EventBus;
import dawn.gfx.Display;
import dawn.gfx.DisplayResizedEvent;
import dawn.gfx.GlStateManager;
import dawn.gfx.MatrixUtils;
import dawn.gfx.Shader;
import dawn.gfx.ShaderProps;
import dawn.gfx.Tessellator;
import dawn.init.Fonts;
import dawn.init.Textures;
import dawn.registry.Identifier;
import lombok.Getter;
import static org.lwjgl.opengl.GL11.glViewport;

public final class RenderManager {

	private final Dawn dawn;
	private final @Getter Shader shader;
	private final @Getter Tessellator tessellator;

	RenderManager(final Dawn dawn, final AssetManager assets) {
		this.dawn = dawn;
		//TODO handle root shader reloading
		this.shader = assets.getShaders().getShader(Identifier.of("root"));
		this.shader.bind();
		this.tessellator = new Tessellator(dawn, assets.getTextures()::getTexture);
		EventBus.register(DisplayResizedEvent.class, _ -> this.resetShader());
		this.resetShader();
	}

	void render() {
		//TODO render gui
		GlStateManager.blendSimple();
		this.tessellator.start()
			.set(Textures.ARGON).pos(0, 0, Display.getWidth(), Display.getHeight() / 2).draw()
			.set(this.dawn.getAssets().getFonts().getFont(Fonts.ROOT).texture()).pos(0, 0).draw()
			.end();
	}

	private void resetShader() {
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, this.shader.getAsset().getPropMatrixProjection()), false, MatrixUtils.ortho());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, this.shader.getAsset().getPropMatrixModelView()), false, MatrixUtils.identity());
	}
}
