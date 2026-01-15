package dawn.lib;

import java.util.BitSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import dawn.gfx.Display;
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

public final class Input {

	private static final int MAX_MOUSE_BUTTONS = GLFW_MOUSE_BUTTON_LAST + 1;
	private static final int MAX_KEYS = GLFW_KEY_LAST + 1;
	private static final Int2ObjectMap<String> KEY_NAMES = new Int2ObjectArrayMap<>();
	private final double[] mouseButtonPositions = new double[Input.MAX_MOUSE_BUTTONS * 2 * 2];
	private int buttonStates = 0;
	private double mouseX = 0;
	private double mouseY = 0;
	private final AtomicBoolean charMode = new AtomicBoolean(false);
	private final BitSet keyStates = new BitSet(Input.MAX_KEYS);
	private final BitSet keyRepeat = new BitSet(Input.MAX_KEYS);
	private final int[] keyMods = new int[Input.MAX_KEYS];
	private final CharPriorityQueue charQueue = CharPriorityQueues.synchronize(new CharArrayFIFOQueue());

	public static String getKeyName(final int keyCode) {
		return Input.KEY_NAMES.computeIfAbsent(keyCode, _ -> Objects.requireNonNullElseGet(GLFW.glfwGetKeyName(keyCode, -1), () -> Input.KEY_NAMES.get(GLFW.GLFW_KEY_UNKNOWN)));
	}

	public void handleMousePos(final double x, final double y) {
		this.mouseX = x;
		this.mouseY = y;
	}

	public void handleMouseButton(final int button, final int action, final boolean allowDuplicateEvents) {
		if (button < Input.MAX_MOUSE_BUTTONS) {
			final int mask = 1 << button;
			int currentState = this.buttonStates & 0xFF;
			final boolean pressed = (currentState & mask) != 0;
			if (!allowDuplicateEvents && pressed == (action == GLFW_PRESS)) {
				return;
			}
			this.buttonStates = (this.buttonStates & 0x00FF) | (currentState << 8);
			final int currentIndex = button << 1;
			final int prevIndex = (button + Input.MAX_MOUSE_BUTTONS) << 1;
			this.mouseButtonPositions[prevIndex] = this.mouseButtonPositions[currentIndex];
			this.mouseButtonPositions[prevIndex + 1] = this.mouseButtonPositions[currentIndex + 1];
			if (action == GLFW_PRESS) {
				currentState |= mask;
			} else {
				currentState &= ~mask;
			}
			this.buttonStates = (this.buttonStates & 0xFF00) | currentState;
			this.mouseButtonPositions[currentIndex] = this.getMouseX();
			this.mouseButtonPositions[currentIndex + 1] = this.getMouseY();
		}
	}

	public boolean isMouseDown(final int button) {
		return ((this.buttonStates >> button) & 1) == 1;
	}

	public boolean isMouseDownLeft() {
		return this.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT);
	}

	public boolean isMouseDownRight() {
		return this.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
	}

	public boolean isMouseDownMiddle() {
		return this.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
	}

	public boolean wasMouseDown(final int button) {
		return ((this.buttonStates >> button + Input.MAX_MOUSE_BUTTONS) & 1) == 1;
	}

	public boolean wasMouseDownLeft() {
		return this.wasMouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT);
	}

	public boolean wasMouseDownRight() {
		return this.wasMouseDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
	}

	public boolean wasMouseDownMiddle() {
		return this.wasMouseDown(GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
	}

	public double getMouseX() {
		return this.mouseX * Display.getWidth() / Display.getWindowWidth();
	}

	public double getMouseY() {
		return this.mouseY * Display.getHeight() / Display.getWindowHeight();
	}

	public boolean isClicked(final int button, final boolean consume) {
		final boolean wasPressed = this.wasMouseDown(button);
		final boolean isReleased = !this.isMouseDown(button);
		if (wasPressed && isReleased) {
			final int prevIndex = (button + Input.MAX_MOUSE_BUTTONS) << 1;
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

	public boolean isClicked(final int button) {
		return this.isClicked(button, true);
	}

	public boolean isLeftClicked() {
		return this.isClicked(GLFW_MOUSE_BUTTON_LEFT);
	}

	public boolean isRightClicked() {
		return this.isClicked(GLFW_MOUSE_BUTTON_RIGHT);
	}

	public boolean isMiddleClicked() {
		return this.isClicked(GLFW_MOUSE_BUTTON_MIDDLE);
	}

	public void handleKeyEvent(final int key, final int scancode, final int action, final int mods) {
		if (!this.charMode.get() && key < Input.MAX_KEYS) {
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

	public boolean isKeyDown(final int keyCode) {
		if (keyCode < 0 || keyCode >= Input.MAX_KEYS) {
			return false;
		}
		return !this.charMode.get() && this.keyStates.get(keyCode);
	}

	public boolean isKeyRepeating(final int keyCode) {
		if (keyCode < 0 || keyCode >= Input.MAX_KEYS) {
			return false;
		}
		return !this.charMode.get() && this.keyRepeat.get(keyCode);
	}

	public int getKeyMods(final int keyCode) {
		if (keyCode < 0 || keyCode >= Input.MAX_KEYS) {
			return 0;
		}
		return this.keyMods[keyCode];
	}

	public boolean hasKeyModShift(final int keyCode) {
		return (this.getKeyMods(keyCode) & GLFW_MOD_SHIFT) == GLFW_MOD_SHIFT;
	}

	public boolean hasKeyModCtrl(final int keyCode) {
		return (this.getKeyMods(keyCode) & GLFW_MOD_CONTROL) == GLFW_MOD_CONTROL;
	}

	public boolean hasKeyModAlt(final int keyCode) {
		return (this.getKeyMods(keyCode) & GLFW_MOD_ALT) == GLFW_MOD_ALT;
	}

	public boolean hasKeyModSuper(final int keyCode) {
		return (this.getKeyMods(keyCode) & GLFW_MOD_SUPER) == GLFW_MOD_SUPER;
	}

	static {
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_UNKNOWN, "[?]");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_SPACE, "Space");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_APOSTROPHE, "'");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_COMMA, ",");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_MINUS, "-");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_PERIOD, ".");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_SLASH, "/");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_0, "0");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_1, "1");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_2, "2");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_3, "3");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_4, "4");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_5, "5");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_6, "6");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_7, "7");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_8, "8");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_9, "9");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_SEMICOLON, ";");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_EQUAL, "=");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_A, "A");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_B, "B");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_C, "C");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_D, "D");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_E, "E");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F, "F");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_G, "G");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_H, "H");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_I, "I");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_J, "J");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_K, "K");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_L, "L");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_M, "M");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_N, "N");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_O, "O");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_P, "P");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_Q, "Q");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_R, "R");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_S, "S");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_T, "T");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_U, "U");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_V, "V");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_W, "W");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_X, "X");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_Y, "Y");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_Z, "Z");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_BRACKET, "[");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_BACKSLASH, "\\");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_BRACKET, "[");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_GRAVE_ACCENT, "`");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_ESCAPE, "Escape");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_ENTER, "Enter");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_TAB, "Tab");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_BACKSPACE, "Backspace");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_INSERT, "Insert");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_DELETE, "Delete");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT, "Arrow right");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_LEFT, "Arrow left");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_DOWN, "Arrow down");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_UP, "Arrow up");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_PAGE_UP, "Page up");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_PAGE_DOWN, "Page down");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_HOME, "Home");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_END, "End");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_CAPS_LOCK, "Caps Lock");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_SCROLL_LOCK, "Scroll Lock");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_NUM_LOCK, "Num Lock");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_PRINT_SCREEN, "PrintScreen");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_PAUSE, "Pause");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F1, "F1");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F2, "F2");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F3, "F3");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F4, "F4");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F5, "F5");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F6, "F6");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F7, "F7");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F8, "F8");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F9, "F9");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F10, "F10");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F11, "F11");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F12, "F12");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F13, "F13");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F14, "F14");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F15, "F15");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F16, "F16");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F17, "F17");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F18, "F18");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F19, "F19");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F20, "F20");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F21, "F21");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F22, "F22");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F23, "F23");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F24, "F24");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_F25, "F25");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_0, "Numpad 0");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_1, "Numpad 1");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_2, "Numpad 2");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_3, "Numpad 3");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_4, "Numpad 4");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_5, "Numpad 5");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_6, "Numpad 6");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_7, "Numpad 7");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_8, "Numpad 8");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_9, "Numpad 9");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_DECIMAL, "Decimal");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_DIVIDE, "Divide");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_MULTIPLY, "Multiply");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_SUBTRACT, "Subtract");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_ADD, "Add");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_ENTER, "Numpad enter");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_KP_EQUAL, "Numpad equals");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_SHIFT, "Shift (L)");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_CONTROL, "Control (L)");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_ALT, "Alt (L)");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_SUPER, "Meta (L)");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_SHIFT, "Shift (R)");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_CONTROL, "Control (R)");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_ALT, "Alt (R)");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_SUPER, "Meta (R)");
		Input.KEY_NAMES.put(GLFW.GLFW_KEY_MENU, "Menu");
	}
}
