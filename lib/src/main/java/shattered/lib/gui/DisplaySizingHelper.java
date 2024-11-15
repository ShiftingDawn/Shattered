package shattered.lib.gui;

import shattered.lib.gfx.Display;

public final class DisplaySizingHelper {

	public static int calcX(int current) {
		if (current < 0) {
			current = Display.get().getWidth() / Math.abs(current);
		}
		return current;
	}

	public static int calcY(int current) {
		if (current < 0) {
			current = Display.get().getHeight() / Math.abs(current);
		}
		return current;
	}

	public static int calcWidth(int current) {
		if (current == -1) {
			return Display.get().getWidth();
		} else if (current < 0) {
			current = Display.get().getWidth() / Math.abs(current);
		}
		return current;
	}

	public static int calcHeight(int current) {
		if (current == -1) {
			return Display.get().getHeight();
		} else if (current < 0) {
			current = Display.get().getHeight() / Math.abs(current);
		}
		return current;
	}

	private DisplaySizingHelper() {
	}
}
