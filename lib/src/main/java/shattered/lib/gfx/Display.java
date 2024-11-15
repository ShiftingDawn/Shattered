package shattered.lib.gfx;

import shattered.lib.Internal;

public interface Display {

	static Display get() {
		return Internal.DISPLAY;
	}

	int getWidth();

	int getHeight();
}