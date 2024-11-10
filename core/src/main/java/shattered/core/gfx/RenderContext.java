package shattered.core.gfx;

import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

public final class RenderContext {

	private long windowId;

	public RenderContext() {
	}

	public void init(final long windowId) {
		this.windowId = windowId;

		glfwMakeContextCurrent(windowId);
		GL.createCapabilities();

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glfwSwapBuffers(this.windowId);
		glfwPollEvents();
	}

	public void destroy() {
		Display.destroy();
	}
}
