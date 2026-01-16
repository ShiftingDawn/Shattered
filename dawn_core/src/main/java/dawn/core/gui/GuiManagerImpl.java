package dawn.core.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import dawn.event.EventBus;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gfx.Window;
import dawn.gfx.WindowResizedEvent;
import dawn.gui.GuiBase;
import dawn.gui.GuiManager;
import dawn.gui.GuiScreen;
import dawn.gui.GuiWidget;
import dawn.input.MouseEventType;
import dawn.internal.DawnLib;
import lombok.Getter;

public final class GuiManagerImpl implements GuiManager {

	private final List<GuiScreen> screens = new ArrayList<>();
	private final @Getter Window window;

	public GuiManagerImpl(final Window window) {
		this.window = window;
		window.getInput().addMouseListener(this::handleMouseEvent);
		EventBus.bus().register(WindowResizedEvent.class, e -> {
			if (e.pointer() == window.getPointer()) {
				this.reload();
			}
		});
	}

	@Override
	public void openScreen(final GuiScreen screen) {
		if (this.screens.contains(screen)) {
			//Move to top
			this.screens.remove(screen);
			this.screens.addLast(screen);
			return;
		}
		DawnLib.GUI_MANAGER.set(this);
		screen.getGuiManager(); //Force load the field
		this.screens.addLast(screen);
		screen.init();
	}

	@Override
	public void closeScreen(final GuiScreen screen) {
		this.screens.remove(screen);
	}

	private void reload() {
		this.walkStack(true, screen -> {
			screen.init();
			for (final GuiWidget widget : screen.getWidgets()) {
				widget.init(screen);
			}
		});
	}

	public void tick() {
		this.walkStack(true, screen -> {
			screen.tick();
			for (final GuiWidget widget : screen.getWidgets()) {
				widget.tick();
			}
		});
	}

	public void render(final Tessellator tessellator, final FontRenderer fontRenderer) {
		this.walkStack(true, screen -> {
			screen.renderBackground(tessellator, fontRenderer, this.window.getInput());
			for (final GuiWidget widget : screen.getWidgets()) {
				widget.renderBackground(tessellator, fontRenderer, this.window.getInput());
			}
			screen.renderForeground(tessellator, fontRenderer, this.window.getInput());
			for (final GuiWidget widget : screen.getWidgets()) {
				widget.renderForeground(tessellator, fontRenderer, this.window.getInput());
			}
		});
	}

	private void handleMouseEvent(final int button, final MouseEventType eventType, final double mouseX, final double mouseY) {
		final int mx = (int) mouseX;
		final int my = (int) mouseY;
		this.walkStack(true, screen -> {
			if (screen.contains(mx, my)) {
				if (this.processMouseEvent(screen, button, eventType, mx, my)) {
					return true;
				}
				for (final GuiWidget widget : screen.getWidgets()) {
					if (widget.contains(mx, my) && this.processMouseEvent(widget, button, eventType, mx, my)) {
						return true;
					}
				}
			}
			return false;
		});

	}

	private boolean processMouseEvent(final GuiBase receiver, final int button, final MouseEventType eventType, final int mouseX, final int mouseY) {
		return switch (eventType) {
			case RELEASE -> receiver.onMouseReleased(button, mouseX, mouseY);
			case PRESS -> receiver.onMousePressed(button, mouseX, mouseY);
			case CLICK -> receiver.onMouseClicked(button, mouseX, mouseY);
		};
	}

	private List<GuiScreen> copyStack() {
		return new ArrayList<>(this.screens);
	}

	private void walkStack(final boolean stopAtFullscreen, final Predicate<GuiScreen> action) {
		final List<GuiScreen> list = this.copyStack();
		for (int i = list.size() - 1; i >= 0; --i) {
			final GuiScreen screen = list.get(i);
			if (action.test(screen)) {
				break;
			}
			if (stopAtFullscreen && screen.isFullScreen()) {
				break;
			}
		}
	}

	private void walkStack(final boolean stopAtFullscreen, final Consumer<GuiScreen> action) {
		this.walkStack(stopAtFullscreen, screen -> {
			action.accept(screen);
			return false;
		});
	}
}
