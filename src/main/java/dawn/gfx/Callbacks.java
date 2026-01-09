package dawn.gfx;

import dawn.Dawn;
import dawn.event.Event;
import dawn.event.EventBus;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;

final class Callbacks {

	private static GLFWKeyCallback keyCallback;
	private static GLFWFramebufferSizeCallback framebufferSizeCallback;
	private static GLFWWindowCloseCallback windowCloseCallback;

	public static void init(long window) {
		keyCallback = glfwSetKeyCallback(window, Callbacks::keyCallback);
		framebufferSizeCallback = glfwSetFramebufferSizeCallback(window, Callbacks::framebufferSizeCallback);
		windowCloseCallback = glfwSetWindowCloseCallback(window, Callbacks::windowCloseCallback);
	}

	public static void destroy() {
		if (keyCallback != null) keyCallback.free();
		if (framebufferSizeCallback != null) framebufferSizeCallback.free();
		if (windowCloseCallback != null) windowCloseCallback.free();
	}

	private static void keyCallback(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
			Dawn.getDawn().stop();
		}
	}

	private static void framebufferSizeCallback(long window, int width, int height) {
		int oldWidth = Display.getWidth();
		int oldHeight = Display.getHeight();
		Display.setWidth(width);
		Display.setHeight(height);
		EventBus.post(new DisplayResizedEvent(window, oldWidth, oldHeight, width, height));
	}

	private static void windowCloseCallback(long window) {
		Dawn.getDawn().stop();
	}

	private Callbacks() {
	}
}
