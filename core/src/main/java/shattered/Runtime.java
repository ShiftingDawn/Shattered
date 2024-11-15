package shattered;

import java.util.concurrent.atomic.AtomicBoolean;
import org.joml.Matrix4f;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.MatrixUtils;
import shattered.lib.gfx.ShaderProgram;
import shattered.lib.gfx.ShaderProps;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gfx.TessellatorImpl;
import shattered.lib.gfx.Window;
import shattered.lib.util.Color;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

final class Runtime {

	private static final int TICKS_PER_SECOND = Integer.getInteger("shattered.runtime.tickrate", 20);
	final AtomicBoolean running = new AtomicBoolean(false);

	ShaderProgram shader;
	Tessellator t;

	public void init() {
		Window.INSTANCE.activate();

		this.shader = new ShaderProgram("/assets/shattered/shader/root.vert", "/assets/shattered/shader/root.frag", "outColor");
		this.shader.bind();

		glViewport(0, 0, Display.get().getWidth(), Display.get().getHeight());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "projectionMatrix"), false, MatrixUtils.ortho());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "modelViewMatrix"), false, new Matrix4f().identity());

		this.t = new TessellatorImpl(this.shader);
	}

	public void start() {
		final int millisPerTick = 1000 / Runtime.TICKS_PER_SECOND;
		this.running.set(true);
		final long lastTickTime = Shattered.clock();
		while (this.running.get()) {
			final long currentTime = Shattered.clock();
			long delta = currentTime - lastTickTime;

			while (delta >= millisPerTick) {
				this.tick();
				delta -= millisPerTick;
			}
			this.render();
		}
	}

	private void tick() {

	}

	private void render() {
		glClearColor(0.6f, 0.7f, 0.8f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

		//Render here
		this.t.start().set(100, 100, 250, 250).pushMatrix(m -> m.translate(100, 0, 0)).draw(Color.YELLOW).end();

		glfwSwapBuffers(Window.INSTANCE.getWindow());
		glfwPollEvents();
	}
}
