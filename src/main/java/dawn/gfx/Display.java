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
	private static @Getter int windowWidth;
	private static @Getter int windowHeight;
	private static @Getter int frameBufferWidth;
	private static @Getter int frameBufferHeight;
	private static int logicalWidth = 320;
	private static int logicalHeight = 240;

	static void setWindowSize(final int width, final int height) {
		Display.windowWidth = width;
		Display.windowHeight = height;
	}

	static void setFrameBufferSize(final int width, final int height) {
		Display.frameBufferWidth = width;
		Display.frameBufferHeight = height;
	}

	static void onPhysicalSizeChanged() {
		final int scale = Display.calculateScale(Display.PREFERRED_SCALE);
		Display.setScale(scale);
		EventBus.post(new DisplayResizedEvent());
	}

	static void setScale(final int guiScale) {
		final int scaleX = (int) (Display.frameBufferWidth / (double) guiScale);
		Display.logicalWidth = Display.frameBufferWidth / (double) guiScale > scaleX ? scaleX + 1 : scaleX;
		final int scaleY = (int) (Display.frameBufferHeight / (double) guiScale);
		Display.logicalHeight = Display.frameBufferHeight / (double) guiScale > scaleY ? scaleY + 1 : scaleY;
	}

	public static int getWidth() {
		return Display.logicalWidth;
	}

	public static int getHeight() {
		return Display.logicalHeight;
	}

	private static int calculateScale(final int maxScale) {
		int result = 1;
		while (result != maxScale && result < Display.frameBufferWidth && result < Display.frameBufferHeight && Display.frameBufferWidth / (result + 1) >= 320 && Display.frameBufferHeight / (result + 1) >= 240) {
			++result;
		}
		return result;
	}

	private Display() {
	}
}
