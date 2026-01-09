package dawn.gfx;

import dawn.Dawn;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;

final class Callbacks {

	private static GLFWKeyCallback keyCallback;
	private static GLFWWindowCloseCallback windowCloseCallback;

	public static void init(long window) {
		keyCallback = glfwSetKeyCallback(window, Callbacks::keyCallback);
		windowCloseCallback = glfwSetWindowCloseCallback(window, Callbacks::windowCloseCallback);
	}

	public static void destroy() {
		if (keyCallback != null) keyCallback.free();
		if (windowCloseCallback != null) windowCloseCallback.free();
	}

	private static void keyCallback(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
			Dawn.getDawn().stop();
		}
	}

	private static void windowCloseCallback(long window) {
		Dawn.getDawn().stop();
	}

	private Callbacks() {
	}
}
