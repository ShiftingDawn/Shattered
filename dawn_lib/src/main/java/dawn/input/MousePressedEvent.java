package dawn.input;

public final class MousePressedEvent extends MouseEvent {

	public MousePressedEvent(final int button, final double xPos, final double yPos) {
		super(button, xPos, yPos);
	}
}
