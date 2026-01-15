package dawn.gfx;

import dawn.Dawn;
import dawn.lib.Input;
import org.jspecify.annotations.NullUnmarked;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;

@NullUnmarked
final class Callbacks {

	private static GLFWKeyCallback keyCallback;
	private static GLFWCharCallback charCallback;
	private static GLFWCursorPosCallback cursorPosCallback;
	private static GLFWMouseButtonCallback mouseButtonCallback;
	private static GLFWFramebufferSizeCallback framebufferSizeCallback;
	private static GLFWWindowSizeCallback windowSizeCallback;
	private static GLFWWindowCloseCallback windowCloseCallback;

	public static void init(final long window, final Input input) {
		Callbacks.keyCallback = glfwSetKeyCallback(window, Callbacks.keyCallback(input));
		Callbacks.charCallback = glfwSetCharCallback(window, Callbacks.charCallback(input));
		Callbacks.cursorPosCallback = glfwSetCursorPosCallback(window, Callbacks.cursorPosCallback(input));
		Callbacks.mouseButtonCallback = glfwSetMouseButtonCallback(window, Callbacks.mouseButtonCallback(input));
		Callbacks.framebufferSizeCallback = glfwSetFramebufferSizeCallback(window, Callbacks::framebufferSizeCallback);
		Callbacks.windowSizeCallback = glfwSetWindowSizeCallback(window, Callbacks::windowSizeCallback);
		Callbacks.windowCloseCallback = glfwSetWindowCloseCallback(window, Callbacks::windowCloseCallback);
	}

	public static void destroy() {
		if (Callbacks.keyCallback != null) {
			Callbacks.keyCallback.free();
		}
		if (Callbacks.charCallback != null) {
			Callbacks.charCallback.free();
		}
		if (Callbacks.cursorPosCallback != null) {
			Callbacks.cursorPosCallback.free();
		}
		if (Callbacks.mouseButtonCallback != null) {
			Callbacks.mouseButtonCallback.free();
		}
		if (Callbacks.framebufferSizeCallback != null) {
			Callbacks.framebufferSizeCallback.free();
		}
		if (Callbacks.windowSizeCallback != null) {
			Callbacks.windowSizeCallback.free();
		}
		if (Callbacks.windowCloseCallback != null) {
			Callbacks.windowCloseCallback.free();
		}
	}

	private static GLFWKeyCallbackI keyCallback(Input input) {
		return (window, key, scancode, action, mods) -> on(window, () -> input.handleKeyEvent(key, scancode, action, mods));
	}

	private static GLFWCharCallbackI charCallback(Input input) {
		return (window, codepoint) -> on(window, () -> input.handleCharEvent(codepoint));
	}

	private static GLFWMouseButtonCallbackI mouseButtonCallback(final Input input) {
		return (window, button, action, mods) -> Callbacks.on(window, () -> input.handleMouseButton(button, action, false));
	}

	private static GLFWCursorPosCallbackI cursorPosCallback(final Input input) {
		return (window, x, y) -> Callbacks.on(window, () -> input.handleMousePos(x, y));
	}

	private static void framebufferSizeCallback(final long window, final int width, final int height) {
		Callbacks.on(window, () -> {
			final int oldWidth = Display.getFrameBufferWidth();
			final int oldHeight = Display.getFrameBufferHeight();
			if (width == 0 || height == 0) {
				return;
			}
			Display.setFrameBufferSize(width, height);
			if (Display.getFrameBufferWidth() != oldWidth || Display.getFrameBufferHeight() != oldHeight) {
				Display.onPhysicalSizeChanged();
			}
		});
	}

	private static void windowSizeCallback(final long window, final int wwidth, final int height) {
		Callbacks.on(window, () -> Display.setWindowSize(wwidth, height));
	}

	private static void windowCloseCallback(final long window) {
		Callbacks.on(window, () -> Dawn.getDawn().stop());
	}

	private static void on(final long windowId, final Runnable callback) {
		if (windowId == Display.getWindow()) {
			callback.run();
		}
	}

	private Callbacks() {
	}
}
