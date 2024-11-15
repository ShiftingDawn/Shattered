package shattered.lib.gfx;

import shattered.lib.Internal;
import shattered.lib.util.Input;

public interface Display {

	static Display get() {
		return Internal.DISPLAY;
	}

	static Input input() {
		return Internal.INPUT;
	}

	int getWidth();

	int getHeight();
}
