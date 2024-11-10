package shattered.core.gfx;

import shattered.core.Shattered;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;

final class GlfwCallbacks {

	public static void register(final long windowId) {
		glfwSetFramebufferSizeCallback(windowId, (window, width, height) -> {
			Display.windowWidth = width;
			Display.windowHeight = height;
		});
		glfwSetWindowCloseCallback(windowId, ignored -> {
			Shattered.getShattered().stop();
		});
	}
}
