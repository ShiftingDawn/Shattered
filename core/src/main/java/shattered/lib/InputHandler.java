package shattered.lib;

import java.util.BitSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import shattered.lib.util.Input;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

public final class InputHandler implements Input {

	public static final Input INSTANCE = new InputHandler();
	private static final BitSet KEY_STATE = new BitSet(512);
	private static final BitSet KEY_REPEAT = new BitSet(512);
	private static final BitSet MOUSE_STATE = new BitSet(8);
	private static final AtomicBoolean CHAR_MODE = new AtomicBoolean(false);
	private static final Queue<Character> CHAR_QUEUE = new ConcurrentLinkedQueue<>();

	private InputHandler() {
	}

	@Override
	public boolean isKeyDown(int key) {
		return KEY_STATE.get(key);
	}

	@Override
	public boolean isMouseDown(int button) {
		return MOUSE_STATE.get(button);
	}

	public static void handleKeyEvent(final int key, final int scancode, final int action, final int mods) {
		if (!InputHandler.CHAR_MODE.get()) {
			InputHandler.KEY_STATE.set(key, action != GLFW_RELEASE);
			InputHandler.KEY_REPEAT.set(key, action == GLFW_REPEAT);
		}
	}

	public static void handleCharEvent(final int codepoint) {
		if (InputHandler.CHAR_MODE.get()) {
			InputHandler.CHAR_QUEUE.offer((char) codepoint);
		}
	}

	public static void handleMouseEvent(final int button, final int action) {
		InputHandler.MOUSE_STATE.set(button, action == GLFW_PRESS);
	}
}
