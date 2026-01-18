package dawn.input;

import dawn.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
sealed abstract class MouseEvent implements Event permits MouseClickEvent, MousePressedEvent, MouseReleasedEvent {

	private final int button;
	private final double xPos;
	private final double yPos;
}
