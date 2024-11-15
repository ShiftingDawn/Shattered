package shattered.lib.util;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public interface Input {

	int MOUSE_LEFT = GLFW_MOUSE_BUTTON_LEFT;
	int MOUSE_RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
	int MOUSE_MIDDLE = GLFW_MOUSE_BUTTON_MIDDLE;

	boolean isKeyDown(int key);

	default boolean isShiftDown() {
		return this.isKeyDown(GLFW_KEY_LEFT_SHIFT) || this.isKeyDown(GLFW_KEY_RIGHT_SHIFT);
	}

	default boolean isControlDown() {
		return this.isKeyDown(GLFW_KEY_LEFT_CONTROL) || this.isKeyDown(GLFW_KEY_RIGHT_CONTROL);
	}

	default boolean isAltDown() {
		return this.isKeyDown(GLFW_KEY_LEFT_ALT) || this.isKeyDown(GLFW_KEY_RIGHT_ALT);
	}

	default boolean isSuperDown() {
		return this.isKeyDown(GLFW_KEY_LEFT_SUPER) || this.isKeyDown(GLFW_KEY_RIGHT_SUPER);
	}

	boolean isMouseDown(int button);
}
