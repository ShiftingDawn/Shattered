package dawn.gfx;

import dawn.event.EventBus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public final class Display {

	//TODO replace with config
	private static final int PREFERRED_SCALE = 0;
	@Setter(AccessLevel.PACKAGE)
	private static @Getter long window;
	private static @Getter int physicalWidth;
	private static @Getter int physicalHeight;
	private static int logicalWidth = 600;
	private static int logicalHeight = 480;

	static void setPhysicalSize(final int width, final int height) {
		Display.physicalWidth = width;
		Display.physicalHeight = height;
	}

	static void onPhysicalSizeChanged() {
		final int scale = Display.calculateScale(Display.PREFERRED_SCALE);
		Display.setScale(scale);
		EventBus.post(new DisplayResizedEvent());
	}

	static void setScale(final int guiScale) {
		final int scaleX = (int) (Display.physicalWidth / (double) guiScale);
		Display.logicalWidth = Display.physicalWidth / (double) guiScale > scaleX ? scaleX + 1 : scaleX;
		final int scaleY = (int) (Display.physicalHeight / (double) guiScale);
		Display.logicalHeight = Display.physicalHeight / (double) guiScale > scaleY ? scaleY + 1 : scaleY;
	}

	public static int getWidth() {
		return Display.logicalWidth;
	}

	public static int getHeight() {
		return Display.logicalHeight;
	}

	private static int calculateScale(final int maxScale) {
		int result = 1;
		while (result != maxScale && result < Display.physicalWidth && result < Display.physicalHeight && Display.physicalWidth / (result + 1) >= 320 && Display.physicalHeight / (result + 1) >= 240) {
			++result;
		}
		return result;
	}

	private Display() {
	}
}
