package shattered;

import java.util.concurrent.atomic.AtomicBoolean;
import shattered.lib.gfx.Window;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

final class Runtime {

	private static final int TICKS_PER_SECOND = Integer.getInteger("shattered.runtime.tickrate", 20);
	final AtomicBoolean running = new AtomicBoolean(false);

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

			glfwPollEvents();
		}
	}

	private void tick() {
		Shattered.getShattered().getGuiManager().tick();
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		Shattered.getShattered().getRenderManager().render();
		glfwSwapBuffers(Window.INSTANCE.getWindow());
	}
}
