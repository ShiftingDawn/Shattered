package shattered.lib.gfx;

import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import shattered.Shattered;
import shattered.lib.InputHandler;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;

final class DisplayCallbacks {

	private static GLFWKeyCallback key;
	private static GLFWCharCallback charCallback;
	private static GLFWMouseButtonCallback mouseButton;
	private static GLFWFramebufferSizeCallback fbSize;
	private static GLFWWindowCloseCallback windowClose;

	public static void register(final long window) {
		glfwSetKeyCallback(window, DisplayCallbacks.key = GLFWKeyCallback.create((ignored, key, scancode, action, mods) -> {
			InputHandler.handleKeyEvent(key, scancode, action, mods);
		}));
		glfwSetCharCallback(window, DisplayCallbacks.charCallback = GLFWCharCallback.create((ignored, codepoint) -> {
			InputHandler.handleCharEvent(codepoint);
		}));
		glfwSetMouseButtonCallback(window, DisplayCallbacks.mouseButton = GLFWMouseButtonCallback.create((ignored, button, action, mods) -> {
			InputHandler.handleMouseEvent(button, action);
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
		DisplayCallbacks.charCallback.free();
		DisplayCallbacks.mouseButton.free();
		DisplayCallbacks.fbSize.free();
		DisplayCallbacks.windowClose.free();
	}

	private DisplayCallbacks() {
	}
}
