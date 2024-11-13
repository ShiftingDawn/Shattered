package shattered.lib.gfx;

import java.nio.IntBuffer;
import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.nglfwGetFramebufferSize;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public final class Display {

	@Getter
	private static long window;
	@Getter
	private static int width = 800;
	@Getter
	private static int height = 600;

	private static GLFWErrorCallback errorCallback;
	private static GLFWKeyCallback keyCallback;
	private static GLFWFramebufferSizeCallback fbCallback;

	public static void init() {
		glfwSetErrorCallback(Display.errorCallback = GLFWErrorCallback.createPrint(System.err));
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_SAMPLES, 4);

		final GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		assert vidMode != null;
		glfwWindowHint(GLFW_RED_BITS, vidMode.redBits());
		glfwWindowHint(GLFW_GREEN_BITS, vidMode.greenBits());
		glfwWindowHint(GLFW_BLUE_BITS, vidMode.blueBits());

		Display.window = glfwCreateWindow(Display.width, Display.height, "Hello shaders!", NULL, NULL);
		if (Display.window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		glfwSetKeyCallback(Display.window, Display.keyCallback = new GLFWKeyCallback() {

			@Override
			public void invoke(final long window, final int key, final int scancode, final int action, final int mods) {
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
					glfwSetWindowShouldClose(window, true);
				}
			}
		});
		glfwSetFramebufferSizeCallback(Display.window, Display.fbCallback = new GLFWFramebufferSizeCallback() {

			@Override
			public void invoke(final long window, final int w, final int h) {
				if (w > 0 && h > 0) {
					Display.width = w;
					Display.height = h;
				}
			}
		});

		final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(Display.window, (vidmode.width() - Display.width) / 2, (vidmode.height() - Display.height) / 2);
		glfwShowWindow(Display.window);

		final IntBuffer framebufferSize = BufferUtils.createIntBuffer(2);
		nglfwGetFramebufferSize(Display.window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
		Display.width = framebufferSize.get(0);
		Display.height = framebufferSize.get(1);
	}

	public static void destroy() {
		glfwDestroyWindow(Display.window);
		Display.keyCallback.free();
		Display.fbCallback.free();
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private Display() {
	}
}
