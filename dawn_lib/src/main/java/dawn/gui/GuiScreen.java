package dawn.gui;

import java.util.ArrayList;
import java.util.List;
import dawn.internal.DawnLib;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

public abstract class GuiScreen extends GuiBase {

	private final @Getter List<GuiWidget> widgets = new ArrayList<>();
	private @Nullable GuiManager guiManager;
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
		widget.setScreen(this);
		this.widgets.addLast(widget);
		return widget;
	}

	public void remove(final GuiWidget widget) {
		this.widgets.remove(widget);
	}

	public final void setPos(final int x, final int y) {
		this.setX(x);
		this.setY(y);
	}

	public final void setSize(final int width, final int height) {
		this.setWidth(width);
		this.setHeight(height);
	}

	protected final void setFullscreen() {
		this.setWidth(this.getDisplayWidth());
		this.setHeight(this.getDisplayHeight());
	}

	public final GuiManager getGuiManager() {
		if (this.guiManager == null) {
			this.guiManager = DawnLib.INSTANCE.getGuiManager();
		}
		return this.guiManager;
	}

	public final boolean isFullScreen() {
		return this.getWidth() == this.getDisplayWidth() && this.getHeight() == this.getDisplayHeight();
	}

	public final int getDisplayWidth() {
		//noinspection ConstantValue
		return this.getGuiManager() != null ? this.getGuiManager().getWindow().getWidth() : 0;
	}

	public final int getDisplayHeight() {
		//noinspection ConstantValue
		return this.getGuiManager() != null ? this.getGuiManager().getWindow().getHeight() : 0;
	}

	public boolean allowMultipleInstances() {
		return false;
	}
}
