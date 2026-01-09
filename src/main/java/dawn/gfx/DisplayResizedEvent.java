package dawn.gfx;

import dawn.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class DisplayResizedEvent extends Event {

	private final long window;
	private final int oldWidth;
	private final int oldHeight;
	private final int newWidth;
	private final int newHeight;
}
