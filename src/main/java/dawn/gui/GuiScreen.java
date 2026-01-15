package dawn.gui;

import java.util.ArrayList;
import java.util.List;
import dawn.gfx.Display;
import dawn.lib.Util;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class GuiScreen extends GuiBase {

	private final @Getter List<GuiWidget> widgets = new ArrayList<>();
	@Setter(AccessLevel.PROTECTED)
	private @Getter int width = 176;
	@Setter(AccessLevel.PROTECTED)
	private @Getter int height = 166;
	@Setter(AccessLevel.PROTECTED)
	private @Getter int x = 0;
	@Setter(AccessLevel.PROTECTED)
	private @Getter int y = 0;

	public void init() {
		this.x = (Display.getWidth() - this.width) / 2;
		this.y = (Display.getHeight() - this.height) / 2;
	}

	public <T extends GuiWidget> T add(final T widget) {
		return Util.make(widget, this.widgets::addLast);
	}

	public void remove(final GuiWidget widget) {
		this.widgets.remove(widget);
	}

	protected final void setFullscreen() {
		this.setWidth(Display.getWidth());
		this.setHeight(Display.getHeight());
	}

	public final boolean isFullScreen() {
		return this.getWidth() == Display.getWidth() && this.getHeight() == Display.getHeight();
	}
}
