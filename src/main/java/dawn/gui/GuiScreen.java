package dawn.gui;

import java.util.ArrayList;
import java.util.List;
import dawn.lib.Util;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class GuiScreen extends GuiBase {

	private final @Getter List<GuiWidget> widgets = new ArrayList<>();
	@Setter(AccessLevel.PACKAGE)
	private @Getter GuiManager guiManager;
	@Setter(AccessLevel.PROTECTED)
	private @Getter int width = 176;
	@Setter(AccessLevel.PROTECTED)
	private @Getter int height = 166;
	@Setter(AccessLevel.PROTECTED)
	private @Getter int x = 0;
	@Setter(AccessLevel.PROTECTED)
	private @Getter int y = 0;

	public void init() {
		this.x = (this.getDisplayWidth() - this.width) / 2;
		this.y = (this.getDisplayHeight() - this.height) / 2;
	}

	public <T extends GuiWidget> T add(final T widget) {
		return Util.make(widget, this.widgets::addLast);
	}

	public void remove(final GuiWidget widget) {
		this.widgets.remove(widget);
	}

	protected final void setFullscreen() {
		this.setWidth(this.getDisplayWidth());
		this.setHeight(this.getDisplayHeight());
	}

	public final boolean isFullScreen() {
		return this.getWidth() == this.getDisplayWidth() && this.getHeight() == this.getDisplayHeight();
	}

	public final int getDisplayWidth() {
		return this.guiManager != null ? this.guiManager.getWindow().getWidth() : 0;
	}

	public final int getDisplayHeight() {
		return this.guiManager != null ? this.guiManager.getWindow().getHeight() : 0;
	}
}
