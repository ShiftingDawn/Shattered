package dawn.core.gfx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import dawn.Dawn;
import dawn.Identifier;
import dawn.core.DawnImpl;
import dawn.core.ExitException;
import dawn.event.EventBus;
import dawn.event.SubscriberToken;
import dawn.gfx.Monitor;
import dawn.gfx.MonitorVideoMode;
import dawn.gfx.Window;
import dawn.lib.RunOnce;
import dawn.lib.option.OptionChangedEvent;
import dawn.lib.option.OptionSupplier;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import static dawn.init.Options.ENABLE_VERTICAL_SYNC;
import static dawn.init.Options.FULLSCREEN;
import static dawn.init.Options.GUI_SCALE;
import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F10;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F11;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F12;
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
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class WindowImpl implements Window, AutoCloseable {

	private static final int MIN_WIDTH = 320;
	private static final int MIN_HEIGHT = 240;
	private static final RunOnce GLFW_INITIALIZED = new RunOnce();
	private final @Getter long pointer;
	private final int[] size = new int[2];
	private final int[] beforeFullscreen = Dawn.make(new int[2], arr -> Arrays.fill(arr, -1));
	private final int[] framebufferSize = new int[2];
	private final int[] logicalSize = new int[2];
	private final Set<@Nullable Callback> callbacks = new HashSet<>();
	private final @Getter InputImpl input = new InputImpl(this);
	private final Runnable closeCallback;
	private final SubscriberToken optionEventListener;
	private int guiScale = 0;

	public WindowImpl(final int windowWidth, final int windowHeight, final Runnable closeCallback, final OptionSupplier options) {
		this.size[0] = windowWidth;
		this.size[1] = windowHeight;
		this.closeCallback = closeCallback;
		this.optionEventListener = EventBus.bus().register(OptionChangedEvent.class, this, this::onOptionsChanged);
		this.guiScale = options.getGuiScale().getAsInt();
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_SAMPLES, 4);
		DawnImpl.LOGGER.info("Discovered monitors:");
		for (final Monitor monitor : MonitorImpl.list()) {
			DawnImpl.LOGGER.info(monitor);
			for (final MonitorVideoMode videoMode : monitor.allModes()) {
				DawnImpl.LOGGER.info("  > {}", videoMode);
			}
		}
		final Monitor monitor = MonitorImpl.getPrimary();
		final MonitorVideoMode vidMode = Objects.requireNonNull(monitor.currentMode(), "Could not retrieve monitor configuration");
		glfwWindowHint(GLFW_RED_BITS, vidMode.redBits());
		glfwWindowHint(GLFW_GREEN_BITS, vidMode.greenBits());
		glfwWindowHint(GLFW_BLUE_BITS, vidMode.blueBits());
		this.pointer = glfwCreateWindow(this.getWindowWidth(), this.getWindowHeight(), DawnImpl.NAME, NULL, NULL);
		if (this.pointer == NULL) {
			DawnImpl.LOGGER.fatal("Could not create window");
			throw new ExitException();
		}
		WindowImpl.loadIcon(this.pointer);
		this.reloadFrameBufferSize();
		this.setupCallbacks();
		glfwShowWindow(this.pointer);
		if (options.isFullscreen().getAsBoolean()) {
			this.setFullscreenState(true);
		}
		glfwMakeContextCurrent(this.pointer);
		glfwSwapInterval(options.enableVerticalSync().getAsBoolean() ? 1 : 0);
		GL.createCapabilities();
		this.onFrameBufferSizeChanged();
	}

	private static void loadIcon(final long window) {
		try (InputStream stream = DawnImpl.class.getResourceAsStream(String.format("/assets/%s/texture/icon.png", Identifier.DEFAULT_DOMAIN))) {
			if (stream == null) {
				throw new FileNotFoundException();
			}
			final byte[] bytes = stream.readAllBytes();
			final ByteBuffer imageBuffer = Dawn.make(BufferUtils.createByteBuffer(bytes.length), buffer -> {
				buffer.put(bytes);
				buffer.flip();
			});
			try (MemoryStack stack = stackPush()) {
				final IntBuffer widthPtr = stack.mallocInt(1);
				final IntBuffer heightPtr = stack.mallocInt(1);
				final IntBuffer channelPtr = stack.mallocInt(1);
				final ByteBuffer image = stbi_load_from_memory(imageBuffer, widthPtr, heightPtr, channelPtr, 4);
				if (image == null) {
					throw new IOException();
				}
				final GLFWImage iconImage = GLFWImage.malloc();
				iconImage.set(widthPtr.get(), heightPtr.get(), image);
				final GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
				iconBuffer.put(0, iconImage);
				glfwSetWindowIcon(window, iconBuffer);
				iconBuffer.free();
				iconImage.free();
				stbi_image_free(image);
			}
		} catch (final IOException e) {
			DawnImpl.LOGGER.error("Could not set window icon", e);
		}
	}

	private void reloadFrameBufferSize() {
		try (final MemoryStack stack = stackPush()) {
			final IntBuffer widthPtr = stack.mallocInt(1);
			final IntBuffer heightPtr = stack.mallocInt(1);
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
			switch (key) {
				case GLFW_KEY_F10 -> this.setFullscreenState(false);
				case GLFW_KEY_F11 -> this.setFullscreenState(true);
				case GLFW_KEY_F12 -> this.setFullscreenState(true);
			}
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
		this.setScale(this.guiScale);
		EventBus.bus().post(new WindowResizedEventImpl(this.pointer));
	}

	private void onOptionsChanged(final OptionChangedEvent event) {
		switch (event.key()) {
			case GUI_SCALE -> {
				this.guiScale = event.options().getGuiScale().getAsInt();
				this.onFrameBufferSizeChanged();
			}
			case ENABLE_VERTICAL_SYNC -> glfwSwapInterval(event.options().enableVerticalSync().getAsBoolean() ? 1 : 0);
			case FULLSCREEN -> this.setFullscreenState(event.options().isFullscreen().getAsBoolean());
		}
	}

	public void update() {
		glfwSwapBuffers(this.pointer);
		glfwPollEvents();
	}

	private void setFullscreenState(final boolean fullscreen) {
		if (!fullscreen) {
			if (this.beforeFullscreen[0] == -1) {
				return;
			}
			System.arraycopy(this.beforeFullscreen, 0, this.size, 0, 2);
			Arrays.fill(this.beforeFullscreen, -1);
			glfwSetWindowMonitor(this.pointer, NULL, 0, 0, this.size[0], this.size[1], GLFW_DONT_CARE);
		} else {
			if (this.beforeFullscreen[0] != -1) {
				return;
			}
			System.arraycopy(this.size, 0, this.beforeFullscreen, 0, 2);
			final long monitor = glfwGetPrimaryMonitor();
			final GLFWVidMode vidMode = glfwGetVideoMode(monitor);
			assert vidMode != null;
			glfwSetWindowMonitor(this.pointer, monitor, 0, 0, vidMode.width(), vidMode.height(), GLFW_DONT_CARE);
			this.size[0] = vidMode.width();
			this.size[1] = vidMode.height();
		}
	}

	private void setScale(int scale) {
		scale = WindowImpl.calculateScale(scale, this.getFramebufferWidth(), this.getFramebufferHeight());
		final int scaleX = (int) (this.getFramebufferWidth() / (double) scale);
		this.logicalSize[0] = this.getFramebufferWidth() / (double) scale > scaleX ? scaleX + 1 : scaleX;
		final int scaleY = (int) (this.getFramebufferHeight() / (double) scale);
		this.logicalSize[1] = this.getFramebufferHeight() / (double) scale > scaleY ? scaleY + 1 : scaleY;
	}

	@Override
	public int getWidth() {
		return this.logicalSize[0];
	}

	@Override
	public int getHeight() {
		return this.logicalSize[1];
	}

	@Override
	public int getWindowWidth() {
		return this.size[0];
	}

	@Override
	public int getWindowHeight() {
		return this.size[1];
	}

	@Override
	public int getFramebufferWidth() {
		return this.framebufferSize[0];
	}

	@Override
	public int getFramebufferHeight() {
		return this.framebufferSize[1];
	}

	private static int calculateScale(final int preferredScale, final int frameBufferWidth, final int frameBufferHeight) {
		int result = 1;
		while (result != preferredScale && result < frameBufferWidth && result < frameBufferHeight
			&& frameBufferWidth / (result + 1) >= WindowImpl.MIN_WIDTH && frameBufferHeight / (result + 1) >= WindowImpl.MIN_HEIGHT) {
			++result;
		}
		return result;
	}

	@Override
	public void close() {
		this.freeCallbacks();
		this.optionEventListener.unsubscribe();
		glfwDestroyWindow(this.pointer);
	}

	public static void initGlfw() {
		WindowImpl.GLFW_INITIALIZED.test(() -> "GLFW has already been initialized");
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) {
			DawnImpl.LOGGER.fatal("Could not initialize GLFW");
			throw new ExitException();
		}
	}

	public static void destroyGlfw() {
		glfwTerminate();
		//noinspection resource,DataFlowIssue
		glfwSetErrorCallback(null).free();
	}
}
