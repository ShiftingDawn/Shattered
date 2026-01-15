package dawn.gfx;

import dawn.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class WindowResizedEvent extends Event {

	private final long pointer;
}
