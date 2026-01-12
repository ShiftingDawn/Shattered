package dawn.gfx;

import java.nio.IntBuffer;
import dawn.Dawn;
import dawn.lib.ExitException;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PLATFORM;
import static org.lwjgl.glfw.GLFW.GLFW_PLATFORM_WAYLAND;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
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
import static org.lwjgl.glfw.GLFW.glfwInitHint;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPlatformSupported;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.nglfwGetFramebufferSize;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public final class GlfwSetup {

	public static final Logger LOGGER = Dawn.getLogger("Display");

	public static void init(final int displayWidth, final int displayHeight) {
		Display.setWidth(displayWidth);
		Display.setHeight(displayHeight);
		if (glfwPlatformSupported(GLFW_PLATFORM_WAYLAND)) {
			glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_WAYLAND);
		}
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) {
			GlfwSetup.LOGGER.fatal("Could not initialize GLFW");
			throw new ExitException();
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_SAMPLES, 4);

		final GLFWVidMode monitorMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		assert monitorMode != null;
		glfwWindowHint(GLFW_RED_BITS, monitorMode.redBits());
		glfwWindowHint(GLFW_GREEN_BITS, monitorMode.greenBits());
		glfwWindowHint(GLFW_BLUE_BITS, monitorMode.blueBits());

		final long window = glfwCreateWindow(Display.getWidth(), Display.getHeight(), Dawn.NAME, NULL, NULL);
		if (window == NULL) {
			GlfwSetup.LOGGER.fatal("Could not create window");
			throw new ExitException();
		}

		Callbacks.init(window);

		glfwShowWindow(window);
		final IntBuffer framebufferSize = BufferUtils.createIntBuffer(2);
		nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
		Display.setWidth(framebufferSize.get(0));
		Display.setHeight(framebufferSize.get(1));
		Display.setWindow(window);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		GL.createCapabilities();
	}

	public static void destroy() {
		glfwDestroyWindow(Display.getWindow());
		Callbacks.destroy();
		glfwTerminate();
		//noinspection resource,DataFlowIssue
		glfwSetErrorCallback(null).free();
	}

	private GlfwSetup() {
	}
}
