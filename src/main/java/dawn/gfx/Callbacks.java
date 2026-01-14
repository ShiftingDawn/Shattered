package dawn.gfx;

import dawn.Dawn;
import org.jspecify.annotations.NullUnmarked;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;

@NullUnmarked
final class Callbacks {

	private static GLFWKeyCallback keyCallback;
	private static GLFWCharCallback charCallback;
	private static GLFWMouseButtonCallback mouseButtonCallback;
	private static GLFWFramebufferSizeCallback framebufferSizeCallback;
	private static GLFWWindowCloseCallback windowCloseCallback;

	public static void init(final long window) {
		Callbacks.keyCallback = glfwSetKeyCallback(window, Callbacks::keyCallback);
		Callbacks.charCallback = glfwSetCharCallback(window, Callbacks::charCallback);
		Callbacks.mouseButtonCallback = glfwSetMouseButtonCallback(window, Callbacks::mouseButtonCallback);
		Callbacks.framebufferSizeCallback = glfwSetFramebufferSizeCallback(window, Callbacks::framebufferSizeCallback);
		Callbacks.windowCloseCallback = glfwSetWindowCloseCallback(window, Callbacks::windowCloseCallback);
	}

	public static void destroy() {
		if (Callbacks.keyCallback != null) {
			Callbacks.keyCallback.free();
		}
		if (Callbacks.charCallback != null) {
			Callbacks.charCallback.free();
		}
		if (Callbacks.mouseButtonCallback != null) {
			Callbacks.mouseButtonCallback.free();
		}
		if (Callbacks.framebufferSizeCallback != null) {
			Callbacks.framebufferSizeCallback.free();
		}
		if (Callbacks.windowCloseCallback != null) {
			Callbacks.windowCloseCallback.free();
		}
	}

	private static void keyCallback(final long window, final int key, final int scancode, final int action, final int mods) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
			Dawn.getDawn().stop();
		}
	}

	private static void charCallback(final long window, final int codepoint) {
	}

	private static void mouseButtonCallback(final long window1, final int button, final int action, final int mods) {
	}

	private static void framebufferSizeCallback(final long window, final int width, final int height) {
		final int oldWidth = Display.getPhysicalWidth();
		final int oldHeight = Display.getPhysicalHeight();
		if (width == 0 || height == 0) {
			return;
		}
		Display.setPhysicalSize(width, height);
		if (Display.getPhysicalWidth() != oldWidth || Display.getPhysicalHeight() != oldHeight) {
			Display.onPhysicalSizeChanged();
		}
	}

	private static void windowCloseCallback(final long window) {
		Dawn.getDawn().stop();
	}

	private Callbacks() {
	}
}
