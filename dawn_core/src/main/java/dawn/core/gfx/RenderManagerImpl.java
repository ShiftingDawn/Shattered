package dawn.core.gfx;

import dawn.asset.AssetManager;
import dawn.asset.Shader;
import dawn.core.DawnImpl;
import dawn.event.EventBus;
import dawn.gfx.FontRenderer;
import dawn.gfx.GlStateManager;
import dawn.gfx.RenderManager;
import dawn.gfx.ShaderProps;
import dawn.gfx.Tessellator;
import dawn.gfx.Window;
import dawn.gfx.WindowResizedEvent;
import dawn.init.Shaders;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

public final class RenderManagerImpl implements RenderManager {

	private final DawnImpl dawn;
	private final Shader shader;
	private final Tessellator tessellator;
	private final FontRenderer fontRenderer;

	public RenderManagerImpl(final DawnImpl dawn, final AssetManager assets) {
		this.dawn = dawn;
		//TODO handle root shader reloading
		this.shader = assets.shaders().get(Shaders.ROOT);
		GlStateManager.gl().bindShader(this.shader.getProgram());
		this.tessellator = new TessellatorImpl(dawn, assets.textures()::get);
		this.fontRenderer = new FontRendererImpl(dawn, assets.fonts()::get);
		EventBus.bus().register(WindowResizedEvent.class, e -> {
			if (e.pointer() == dawn.getWindow().getPointer()) {
				this.resetShader(dawn.getWindow());
			}
		});
		this.resetShader(dawn.getWindow());
		GlStateManager.gl().blendSimple();
	}

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		this.dawn.getGuiManager().render(this.tessellator, this.fontRenderer);
	}

	private void resetShader(final Window window) {
		glViewport(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
		ShaderProps.get().setUniform4(ShaderProps.get().getNamedLocation(this.shader, this.shader.proto().getPropMatrixProjection()), false,
			new Matrix4f().ortho(0, window.getWidth(), window.getHeight(), 0, 1, -1));
		ShaderProps.get().setUniform4(ShaderProps.get().getNamedLocation(this.shader, this.shader.proto().getPropMatrixModelView()), false,
			new Matrix4f());
	}

	@Override
	public Shader getRootShader() {
		return this.shader;
	}

	@Override
	public Tessellator getTessellator() {
		return this.tessellator;
	}

	@Override
	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}
}
