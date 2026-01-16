package dawn.core.gfx;

import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import dawn.input.Input;
import dawn.input.MouseEventListener;
import dawn.input.MouseEventType;
import it.unimi.dsi.fastutil.chars.CharArrayFIFOQueue;
import it.unimi.dsi.fastutil.chars.CharPriorityQueue;
import it.unimi.dsi.fastutil.chars.CharPriorityQueues;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

final class InputImpl implements Input {

	private static final int MAX_MOUSE_BUTTONS = GLFW_MOUSE_BUTTON_LAST + 1;
	private static final int MAX_KEYS = GLFW_KEY_LAST + 1;
	private static final Int2ObjectMap<String> KEY_NAMES = new Int2ObjectArrayMap<>();
	private final double[] mouseButtonPositions = new double[InputImpl.MAX_MOUSE_BUTTONS * 2 * 2];
	private final WindowImpl window;
	private int buttonStates = 0;
	private double mouseX = 0;
	private double mouseY = 0;
	private final AtomicBoolean charMode = new AtomicBoolean(false);
	private final BitSet keyStates = new BitSet(InputImpl.MAX_KEYS);
	private final BitSet keyRepeat = new BitSet(InputImpl.MAX_KEYS);
	private final int[] keyMods = new int[InputImpl.MAX_KEYS];
	private final CharPriorityQueue charQueue = CharPriorityQueues.synchronize(new CharArrayFIFOQueue());
	private final List<MouseEventListener> mouseListeners = new CopyOnWriteArrayList<>();

	InputImpl(final WindowImpl window) {
		this.window = window;
	}

	@Override
	public void addMouseListener(final MouseEventListener listener) {
		this.mouseListeners.add(listener);
	}

	public void handleMousePos(final double x, final double y) {
		this.mouseX = x;
		this.mouseY = y;
	}

	public void handleMouseButton(final int button, final int action, final boolean allowDuplicateEvents) {
		if (button < InputImpl.MAX_MOUSE_BUTTONS) {
			final int mask = 1 << button;
			int currentState = this.buttonStates & 0xFF;
			final boolean pressed = (currentState & mask) != 0;
			final boolean newPressed = action == GLFW_PRESS;
			if (!allowDuplicateEvents && pressed == (newPressed)) {
				return;
			}
			this.buttonStates = (this.buttonStates & 0x00FF) | (currentState << 8);
			final int currentIndex = button << 1;
			final int prevIndex = (button + InputImpl.MAX_MOUSE_BUTTONS) << 1;
			this.mouseButtonPositions[prevIndex] = this.mouseButtonPositions[currentIndex];
			this.mouseButtonPositions[prevIndex + 1] = this.mouseButtonPositions[currentIndex + 1];
			if (newPressed) {
				currentState |= mask;
			} else {
				currentState &= ~mask;
			}
			this.buttonStates = (this.buttonStates & 0xFF00) | currentState;
			final double mx = this.getMouseX();
			final double my = this.getMouseY();
			this.mouseButtonPositions[currentIndex] = mx;
			this.mouseButtonPositions[currentIndex + 1] = my;
			if (this.isClicked(button, false)) {
				this.dispatchMouseEvent(button, MouseEventType.CLICK, mx, my);
			} else {
				this.dispatchMouseEvent(button, newPressed ? MouseEventType.PRESS : MouseEventType.RELEASE, mx, my);
			}
		}
	}

	private void dispatchMouseEvent(final int button, final MouseEventType eventType, final double mouseX, final double mouseY) {
		for (final MouseEventListener listener : this.mouseListeners) {
			listener.onMouseEvent(button, eventType, mouseX, mouseY);
		}
	}

	@Override
	public boolean isMouseDown(final int button) {
		return ((this.buttonStates >> button) & 1) == 1;
	}

	@Override
	public boolean isMouseDownLeft() {
		return this.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT);
	}

	@Override
	public boolean isMouseDownRight() {
		return this.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
	}

	@Override
	public boolean isMouseDownMiddle() {
		return this.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
	}

	@Override
	public boolean wasMouseDown(final int button) {
		return ((this.buttonStates >> button + InputImpl.MAX_MOUSE_BUTTONS) & 1) == 1;
	}

	@Override
	public boolean wasMouseDownLeft() {
		return this.wasMouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT);
	}

	@Override
	public boolean wasMouseDownRight() {
		return this.wasMouseDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
	}

	@Override
	public boolean wasMouseDownMiddle() {
		return this.wasMouseDown(GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
	}

	@Override
	public double getMouseX() {
		return this.mouseX * this.window.getWidth() / this.window.getWindowWidth();
	}

	@Override
	public double getMouseY() {
		return this.mouseY * this.window.getHeight() / this.window.getWindowHeight();
	}

	@Override
	public boolean isClicked(final int button, final boolean consume) {
		final boolean wasPressed = this.wasMouseDown(button);
		final boolean isReleased = !this.isMouseDown(button);
		if (wasPressed && isReleased) {
			final int prevIndex = (button + InputImpl.MAX_MOUSE_BUTTONS) << 1;
			final int currentIndex = button << 1;
			final double prevX = this.mouseButtonPositions[prevIndex];
			final double prevY = this.mouseButtonPositions[prevIndex + 1];
			final double curX = this.mouseButtonPositions[currentIndex];
			final double curY = this.mouseButtonPositions[currentIndex + 1];
			if (Math.abs(curX - prevX) < 5 && Math.abs(curY - prevY) < 5) {
				if (consume) {
					this.handleMouseButton(button, GLFW_RELEASE, true);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isClicked(final int button) {
		return this.isClicked(button, true);
	}

	@Override
	public boolean isLeftClicked() {
		return this.isClicked(GLFW_MOUSE_BUTTON_LEFT);
	}

	@Override
	public boolean isRightClicked() {
		return this.isClicked(GLFW_MOUSE_BUTTON_RIGHT);
	}

	@Override
	public boolean isMiddleClicked() {
		return this.isClicked(GLFW_MOUSE_BUTTON_MIDDLE);
	}

	public void handleKeyEvent(final int key, final int scancode, final int action, final int mods) {
		if (!this.charMode.get() && key < InputImpl.MAX_KEYS) {
			this.keyStates.set(key, action != GLFW_RELEASE);
			this.keyRepeat.set(key, action == GLFW_REPEAT);
			this.keyMods[key] = action == GLFW_RELEASE ? 0 : mods;
		}
	}

	public void handleCharEvent(final int codepoint) {
		if (this.charMode.get()) {
			this.charQueue.enqueue((char) codepoint);
		}
	}

	@Override
	public String getKeyName(final int keyCode) {
		return InputImpl.KEY_NAMES.computeIfAbsent(keyCode, _ -> Objects.requireNonNullElseGet(GLFW.glfwGetKeyName(keyCode, -1), () -> InputImpl.KEY_NAMES.get(GLFW.GLFW_KEY_UNKNOWN)));
	}

	@Override
	public boolean isKeyDown(final int keyCode) {
		if (keyCode < 0 || keyCode >= InputImpl.MAX_KEYS) {
			return false;
		}
		return !this.charMode.get() && this.keyStates.get(keyCode);
	}

	@Override
	public boolean isKeyRepeating(final int keyCode) {
		if (keyCode < 0 || keyCode >= InputImpl.MAX_KEYS) {
			return false;
		}
		return !this.charMode.get() && this.keyRepeat.get(keyCode);
	}

	@Override
	public int getKeyMods(final int keyCode) {
		if (keyCode < 0 || keyCode >= InputImpl.MAX_KEYS) {
			return 0;
		}
		return this.keyMods[keyCode];
	}

	@Override
	public boolean hasKeyModShift(final int keyCode) {
		return (this.getKeyMods(keyCode) & GLFW_MOD_SHIFT) == GLFW_MOD_SHIFT;
	}

	@Override
	public boolean hasKeyModCtrl(final int keyCode) {
		return (this.getKeyMods(keyCode) & GLFW_MOD_CONTROL) == GLFW_MOD_CONTROL;
	}

	@Override
	public boolean hasKeyModAlt(final int keyCode) {
		return (this.getKeyMods(keyCode) & GLFW_MOD_ALT) == GLFW_MOD_ALT;
	}

	@Override
	public boolean hasKeyModSuper(final int keyCode) {
		return (this.getKeyMods(keyCode) & GLFW_MOD_SUPER) == GLFW_MOD_SUPER;
	}
}
