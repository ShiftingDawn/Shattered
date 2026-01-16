package dawn.input;

public interface Input {

	void addMouseListener(final MouseEventListener listener);

	boolean isMouseDown(final int button);

	boolean isMouseDownLeft();

	boolean isMouseDownRight();

	boolean isMouseDownMiddle();

	boolean wasMouseDown(final int button);

	boolean wasMouseDownLeft();

	boolean wasMouseDownRight();

	boolean wasMouseDownMiddle();

	double getMouseX();

	double getMouseY();

	boolean isClicked(final int button, final boolean consume);

	boolean isClicked(final int button);

	boolean isLeftClicked();

	boolean isRightClicked();

	boolean isMiddleClicked();

	String getKeyName(final int keyCode);

	boolean isKeyDown(final int keyCode);

	boolean isKeyRepeating(final int keyCode);

	int getKeyMods(final int keyCode);

	boolean hasKeyModShift(final int keyCode);

	boolean hasKeyModCtrl(final int keyCode);

	boolean hasKeyModAlt(final int keyCode);

	boolean hasKeyModSuper(final int keyCode);
}
