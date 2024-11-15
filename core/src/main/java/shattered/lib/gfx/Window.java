package shattered.lib.gfx;

import java.nio.IntBuffer;
import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import shattered.lib.Internal;
import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
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
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.nglfwGetFramebufferSize;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public final class Window implements Display {

	public static final Window INSTANCE = new Window();
	@Getter
	private long window;
	@Getter
	int width = 800;
	@Getter
	int height = 600;

	public void init() {
		Internal.DISPLAY = this;
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
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

		this.window = glfwCreateWindow(this.width, this.height, "Hello shaders!", NULL, NULL);
		if (this.window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		DisplayCallbacks.register(this.window);

		final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(this.window, (vidmode.width() - this.width) / 2, (vidmode.height() - this.height) / 2);
		glfwShowWindow(this.window);

		final IntBuffer framebufferSize = BufferUtils.createIntBuffer(2);
		nglfwGetFramebufferSize(this.window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
		this.width = framebufferSize.get(0);
		this.height = framebufferSize.get(1);
	}

	public void activate() {
		glfwMakeContextCurrent(this.window);
		glfwSwapInterval(1);
		GL.createCapabilities();
	}

	public void destroy() {
		glfwDestroyWindow(this.window);
		DisplayCallbacks.destroy();
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private Window() {
	}
}
