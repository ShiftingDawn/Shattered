package dawn.gfx;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public final class Display {

	@Setter(AccessLevel.PACKAGE)
	private static @Getter long window;
	@Setter(AccessLevel.PACKAGE)
	private static @Getter int width;
	@Setter(AccessLevel.PACKAGE)
	private static @Getter int height;

	private Display() {
	}
}
