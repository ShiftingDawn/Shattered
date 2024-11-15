package shattered.lib.gfx;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import shattered.Shattered;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

final class DisplayCallbacks {

	private static GLFWKeyCallback key;
	private static GLFWFramebufferSizeCallback fbSize;
	private static GLFWWindowCloseCallback windowClose;

	public static void register(final long window) {
		glfwSetKeyCallback(window, DisplayCallbacks.key = GLFWKeyCallback.create((ignored, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true);
			}
		}));
		glfwSetFramebufferSizeCallback(window, DisplayCallbacks.fbSize = GLFWFramebufferSizeCallback.create((ignored, width, height) -> {
			if (width > 0 && height > 0) {
				Window.INSTANCE.width = width;
				Window.INSTANCE.height = height;
			}
		}));
		glfwSetWindowCloseCallback(window, DisplayCallbacks.windowClose = GLFWWindowCloseCallback.create(ignored -> {
			Shattered.getShattered().stop();
		}));
	}

	public static void destroy() {
		DisplayCallbacks.key.free();
		DisplayCallbacks.fbSize.free();
		DisplayCallbacks.windowClose.free();
	}

	private DisplayCallbacks() {
	}
}
