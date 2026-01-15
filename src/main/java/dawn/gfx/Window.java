package dawn.gfx;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;
import dawn.Dawn;
import dawn.event.EventBus;
import dawn.lib.ExitException;
import dawn.lib.Input;
import dawn.lib.RunOnce;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
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
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwInitHint;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPlatformSupported;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class Window implements AutoCloseable {

	//TODO replace with config
	private static final int PREFERRED_SCALE = 0;
	private static final int MIN_WIDTH = 320;
	private static final int MIN_HEIGHT = 240;
	private static final RunOnce GLFW_INITIALIZED = new RunOnce();
	private final @Getter long pointer;
	private final int[] size = new int[2];
	private final int[] framebufferSize = new int[2];
	private final int[] logicalSize = new int[2];
	private final Set<@Nullable Callback> callbacks = new HashSet<>();
	private final @Getter Input input = new Input(this);
	private final Runnable closeCallback;

	public Window(final int windowWidth, final int windowHeight, final Runnable closeCallback) {
		this.size[0] = windowWidth;
		this.size[1] = windowHeight;
		this.closeCallback = closeCallback;
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
		this.pointer = glfwCreateWindow(this.getWindowWidth(), this.getWindowHeight(), Dawn.NAME, NULL, NULL);
		if (this.pointer == NULL) {
			Dawn.LOGGER.fatal("Could not create window");
			throw new ExitException();
		}
		this.reloadFrameBufferSize();
		this.setupCallbacks();
		glfwShowWindow(this.pointer);
		glfwMakeContextCurrent(this.pointer);
		glfwSwapInterval(1); //TODO make configurable
		GL.createCapabilities();
		this.onFrameBufferSizeChanged();
	}

	private void reloadFrameBufferSize() {
		try (final MemoryStack Stack = MemoryStack.stackPush()) {
			final IntBuffer widthPtr = Stack.mallocInt(1);
			final IntBuffer heightPtr = Stack.mallocInt(1);
			glfwGetWindowSize(this.pointer, widthPtr, heightPtr);
			this.framebufferSize[0] = widthPtr.get(0);
			this.framebufferSize[1] = widthPtr.get(0);
		}
	}

	private void setupCallbacks() {
		this.freeCallbacks();
		this.callbacks.add(glfwSetWindowSizeCallback(this.pointer, this::windowSizeCallback));
		this.callbacks.add(glfwSetFramebufferSizeCallback(this.pointer, this::framebufferSizeCallback));
		this.callbacks.add(glfwSetWindowCloseCallback(this.pointer, this::windowCloseCallback));
		this.callbacks.add(glfwSetKeyCallback(this.pointer, this::keyCallback));
		this.callbacks.add(glfwSetCharCallback(this.pointer, this::charCallback));
		this.callbacks.add(glfwSetCursorPosCallback(this.pointer, this::cursorPosCallback));
		this.callbacks.add(glfwSetMouseButtonCallback(this.pointer, this::mouseButtonCallback));
	}

	private void windowSizeCallback(final long window, final int width, final int height) {
		if (window == this.pointer) {
			this.size[0] = width;
			this.size[1] = height;
		}
	}

	private void framebufferSizeCallback(final long window, final int width, final int height) {
		if (window == this.pointer) {
			final int oldWidth = this.framebufferSize[0];
			final int oldHeight = this.framebufferSize[1];
			if (width == 0 || height == 0) {
				return;
			}
			this.framebufferSize[0] = width;
			this.framebufferSize[1] = height;
			if (width != oldWidth || height != oldHeight) {
				this.onFrameBufferSizeChanged();
			}
		}
	}

	private void windowCloseCallback(final long window) {
		if (window == this.pointer) {
			this.closeCallback.run();
		}
	}

	private void keyCallback(final long window, final int key, final int scancode, final int action, final int mods) {
		if (window == this.pointer) {
			this.input.handleKeyEvent(key, scancode, action, mods);
		}
	}

	private void charCallback(final long window, final int codepoint) {
		if (window == this.pointer) {
			this.input.handleCharEvent(codepoint);
		}
	}

	private void cursorPosCallback(final long window, final double x, final double y) {
		if (window == this.pointer) {
			this.input.handleMousePos(x, y);
		}
	}

	private void mouseButtonCallback(final long window, final int button, final int action, final int mods) {
		if (window == this.pointer) {
			this.input.handleMouseButton(button, action, false);
		}
	}

	private void freeCallbacks() {
		for (final Callback callback : this.callbacks) {
			if (callback != null) {
				callback.free();
			}
		}
		this.callbacks.clear();
	}

	private void onFrameBufferSizeChanged() {
		final int scale = Window.calculateScale(Window.PREFERRED_SCALE, this.getFramebufferWidth(), this.getFramebufferHeight());
		this.setScale(scale);
		EventBus.post(new WindowResizedEvent(this.pointer));
	}

	public void update() {
		glfwSwapBuffers(this.pointer);
		glfwPollEvents();
	}

	public void setScale(final int scale) {
		final int scaleX = (int) (this.getFramebufferWidth() / (double) scale);
		this.logicalSize[0] = this.getFramebufferWidth() / (double) scale > scaleX ? scaleX + 1 : scaleX;
		final int scaleY = (int) (this.getFramebufferHeight() / (double) scale);
		this.logicalSize[1] = this.getFramebufferHeight() / (double) scale > scaleY ? scaleY + 1 : scaleY;
	}

	public int getWindowWidth() {
		return this.size[0];
	}

	public int getWindowHeight() {
		return this.size[1];
	}

	public int getFramebufferWidth() {
		return this.framebufferSize[0];
	}

	public int getFramebufferHeight() {
		return this.framebufferSize[1];
	}

	public int getWidth() {
		return this.logicalSize[0];
	}

	public int getHeight() {
		return this.logicalSize[1];
	}

	private static int calculateScale(final int preferredScale, final int frameBufferWidth, final int frameBufferHeight) {
		int result = 1;
		while (result != preferredScale && result < frameBufferWidth && result < frameBufferHeight
			&& frameBufferWidth / (result + 1) >= Window.MIN_WIDTH && frameBufferHeight / (result + 1) >= Window.MIN_HEIGHT) {
			++result;
		}
		return result;
	}

	@Override
	public void close() {
		this.freeCallbacks();
		glfwDestroyWindow(this.pointer);
	}

	public static void initGlfw() {
		Window.GLFW_INITIALIZED.test(() -> "GLFW has already been initialized");
		if (glfwPlatformSupported(GLFW_PLATFORM_WAYLAND)) {
			glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_WAYLAND);
		}
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) {
			Dawn.LOGGER.fatal("Could not initialize GLFW");
			throw new ExitException();
		}
	}

	public static void destroyGlfw() {
		glfwTerminate();
		//noinspection resource,DataFlowIssue
		glfwSetErrorCallback(null).free();
	}
}
