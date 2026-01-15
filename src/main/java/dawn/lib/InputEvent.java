package dawn.lib;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public sealed abstract class InputEvent permits InputEvent.Mouse {

	@RequiredArgsConstructor
	@Getter
	public static final class Mouse extends InputEvent {

		private final int button;
		private final Action release;

		public enum Action {
			PRESS, RELEASE
		}
	}

}
