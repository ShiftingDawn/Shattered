package shattered;

import lombok.Getter;
import org.joml.Matrix4f;
import shattered.lib.event.EventBus;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.DisplayResizedEvent;
import shattered.lib.gfx.MatrixUtils;
import shattered.lib.gfx.ShaderProgram;
import shattered.lib.gfx.ShaderProps;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gfx.TessellatorImpl;
import shattered.lib.gfx.Window;
import static org.lwjgl.opengl.GL11.glViewport;

public final class RenderManager {

	@Getter
	private final ShaderProgram shader;
	@Getter
	private final Tessellator tessellator;

	RenderManager() {
		Window.INSTANCE.activate();
		this.shader = new ShaderProgram("/assets/shattered/shader/root.vert", "/assets/shattered/shader/root.frag", "outColor");
		this.shader.bind();
		this.tessellator = new TessellatorImpl(this.shader);
		EventBus.bus().register(this::onDisplayResized);
		this.resetShader();
	}

	private void resetShader() {
		glViewport(0, 0, Display.get().getWidth(), Display.get().getHeight());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "projectionMatrix"), false, MatrixUtils.ortho());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "modelViewMatrix"), false, new Matrix4f().identity());
	}

	public void render() {
		Shattered.getShattered().getGuiManager().render(this.tessellator);
	}

	private void onDisplayResized(final DisplayResizedEvent event) {
		this.resetShader();
	}
}
