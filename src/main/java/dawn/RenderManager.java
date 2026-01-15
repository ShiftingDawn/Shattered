package dawn;

import dawn.asset.AssetManager;
import dawn.event.EventBus;
import dawn.gfx.FontRenderer;
import dawn.gfx.GlStateManager;
import dawn.gfx.Shader;
import dawn.gfx.ShaderProps;
import dawn.gfx.Tessellator;
import dawn.gfx.Window;
import dawn.gfx.WindowResizedEvent;
import dawn.registry.Identifier;
import lombok.Getter;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
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
		EventBus.register(WindowResizedEvent.class, e -> {
			if (e.getPointer() == dawn.getWindow().getPointer()) {
				this.resetShader(dawn.getWindow());
			}
		});
		this.resetShader(dawn.getWindow());
		GlStateManager.blendSimple();
	}

	void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

		final int mouseX = (int) this.dawn.getWindow().getInput().getMouseX();
		final int mouseY = (int) this.dawn.getWindow().getInput().getMouseY();
		this.dawn.getGuiManager().render(this.tessellator, this.fontRenderer, mouseX, mouseY);
	}

	private void resetShader(final Window window) {
		glViewport(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, this.shader.getAsset().getPropMatrixProjection()), false,
			new Matrix4f().ortho(0, window.getWidth(), window.getHeight(), 0, 1, -1));
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, this.shader.getAsset().getPropMatrixModelView()), false,
			new Matrix4f());
	}
}
