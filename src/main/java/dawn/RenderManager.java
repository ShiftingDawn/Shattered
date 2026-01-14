package dawn;

import dawn.asset.AssetManager;
import dawn.event.EventBus;
import dawn.gfx.Display;
import dawn.gfx.DisplayResizedEvent;
import dawn.gfx.FontRenderer;
import dawn.gfx.Shader;
import dawn.gfx.ShaderProps;
import dawn.gfx.Tessellator;
import dawn.init.Textures;
import dawn.lib.Color;
import dawn.registry.Identifier;
import lombok.Getter;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL11.glViewport;

public final class RenderManager {

	private final Dawn dawn;
	private final @Getter Shader shader;
	private final @Getter Tessellator tessellator;
	private final @Getter FontRenderer fontRenderer;

	RenderManager(final Dawn dawn, final AssetManager assets) {
		this.dawn = dawn;
		//TODO handle root shader reloading
		this.shader = assets.getShaders().getShader(Identifier.of("root"));
		this.shader.bind();
		this.tessellator = new Tessellator(dawn, assets.getTextures()::getTexture);
		this.fontRenderer = new FontRenderer(dawn, assets.getFonts()::getFont);
		EventBus.register(DisplayResizedEvent.class, _ -> this.resetShader());
		this.resetShader();
	}

	void render() {
		//TODO render gui
		this.tessellator.start()
			.set(Textures.ARGON).pos(0, 0, Display.getWidth(), Display.getHeight()).draw()
			.set(Textures.LOGO).pos(Display.getWidth() / 2 - 85, 30, 170, 21).draw()
			.end();
		this.fontRenderer.start().set("Dit is een font test", Color.GREEN).pos(100, 100).write().end();
		this.tessellator.start().set(Textures.GUI_BACKGROUND).pos(100, 100, 200, 200).draw().end();
	}

	private void resetShader() {
		glViewport(0, 0, Display.getPhysicalWidth(), Display.getPhysicalHeight());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, this.shader.getAsset().getPropMatrixProjection()), false,
			new Matrix4f().ortho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1));
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, this.shader.getAsset().getPropMatrixModelView()), false,
			new Matrix4f());
	}
}
