package dawn.input;

public final class MouseReleasedEvent extends MouseEvent {

	public MouseReleasedEvent(final int button, final double xPos, final double yPos) {
		super(button, xPos, yPos);
	}
}
