package dawn.input;

import dawn.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
sealed abstract class KeyEvent implements Event permits KeyPressedEvent, KeyReleasedEvent, KeyRepeatEvent {

	private final int key;
	private final KeyMods mods;
}
