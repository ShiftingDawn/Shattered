package shattered.core.gfx;

import java.nio.IntBuffer;
import lombok.Getter;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import shattered.core.Shattered;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class Display {

	@Getter
	private static long windowId;
	static int windowWidth = 800;
	static int windowHeight = 600;

	public static void init() {
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

		Display.windowId = glfwCreateWindow(Display.windowWidth, Display.windowHeight, Shattered.NAME, NULL, NULL);
		if (Display.windowId == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		GlfwCallbacks.register(Display.windowId);

		try (MemoryStack stack = stackPush()) {
			final IntBuffer widthBuffer = stack.mallocInt(1);
			final IntBuffer heightBuffer = stack.mallocInt(1);
			glfwGetWindowSize(Display.windowId, widthBuffer, heightBuffer);
			final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(Display.windowId, (vidmode.width() - widthBuffer.get(0)) / 2, (vidmode.height() - heightBuffer.get(0)) / 2);
		}

		glfwMakeContextCurrent(Display.windowId);
		glfwSwapInterval(1);
	}

	public static void openWindow() {
		glfwShowWindow(Display.windowId);
	}

	public static void destroy() {
		glfwFreeCallbacks(Display.windowId);
		glfwDestroyWindow(Display.windowId);
		glfwTerminate();
	}
}
