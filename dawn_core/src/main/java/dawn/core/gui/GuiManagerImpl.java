package dawn.core.gui;

import java.util.ArrayList;
import java.util.List;
import dawn.event.EventBus;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gfx.Window;
import dawn.gfx.WindowResizedEvent;
import dawn.gui.GuiBase;
import dawn.gui.GuiManager;
import dawn.gui.GuiScreen;
import dawn.gui.GuiWidget;
import dawn.gui.RenderPhase;
import dawn.input.EventResult;
import dawn.input.MouseEventType;
import dawn.internal.DawnLib;
import dawn.lib.Rectangle;
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
		final List<GuiScreen> screens = this.copyStack();
		for (int i = screens.size() - 1; i >= 0; --i) {
			final GuiScreen screen = screens.get(i);
			screen.init();
			for (final GuiWidget widget : screen.getWidgets()) {
				widget.init(screen);
			}
		}
	}

	public void tick() {
		final List<GuiScreen> screens = this.copyStack();
		for (int i = screens.size() - 1; i >= 0; --i) {
			final GuiScreen screen = screens.get(i);
			screen.tick();
			for (final GuiWidget widget : screen.getWidgets()) {
				if (!this.isInteractionBlocked(widget, i, screens)) {
					widget.tick();
				}
			}
			if (screen.isFullScreen()) {
				break;
			}
		}
	}

	public void render(final Tessellator tessellator, final FontRenderer fontRenderer) {
		final List<GuiScreen> screens = this.copyStack();
		int renderStartIndex = 0;
		for (int i = screens.size() - 1; i > 0; --i) {
			final GuiScreen screen = screens.get(i);
			if (screen.isFullScreen()) {
				renderStartIndex = i;
				break;
			}
		}
		for (int i = renderStartIndex; i < screens.size(); ++i) {
			final GuiScreen screen = screens.get(i);
			screen.renderBackground(tessellator, fontRenderer, RenderPhase.BACKGROUND, this.window.getInput());
			screen.renderBackground(tessellator, fontRenderer, RenderPhase.INTERACTIVE, this.window.getInput());
			for (final GuiWidget widget : screen.getWidgets()) {
				widget.renderBackground(tessellator, fontRenderer, RenderPhase.BACKGROUND, this.window.getInput());
				if (!this.isInteractionBlocked(widget, i, screens)) {
					widget.renderBackground(tessellator, fontRenderer, RenderPhase.INTERACTIVE, this.window.getInput());
				}
			}
			screen.renderForeground(tessellator, fontRenderer, RenderPhase.BACKGROUND, this.window.getInput());
			screen.renderForeground(tessellator, fontRenderer, RenderPhase.INTERACTIVE, this.window.getInput());
			for (final GuiWidget widget : screen.getWidgets()) {
				widget.renderForeground(tessellator, fontRenderer, RenderPhase.BACKGROUND, this.window.getInput());
				if (!this.isInteractionBlocked(widget, i, screens)) {
					widget.renderForeground(tessellator, fontRenderer, RenderPhase.INTERACTIVE, this.window.getInput());
				}
			}
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted") //It's not
	private boolean isInteractionBlocked(final GuiBase widget, final int currentScreen, final List<GuiScreen> screenStack) {
		final Rectangle widgetBounds = widget.getBounds();
		for (int i = screenStack.size() - 1; i > currentScreen; --i) {
			final GuiScreen screen = screenStack.get(i);
			if (screen.isBlockingInteractionBelow() && screen.getBounds().intersects(widgetBounds)) {
				return true;
			}
		}
		return false;
	}

	private void handleMouseEvent(final int button, final MouseEventType eventType, final double mouseX, final double mouseY) {
		final List<GuiScreen> screens = this.copyStack();
		final int mx = (int) mouseX;
		final int my = (int) mouseY;
		for (int i = screens.size() - 1; i >= 0; --i) {
			final GuiScreen screen = screens.get(i);
			if (screen.contains(mx, my)) {
				for (final GuiWidget widget : screen.getWidgets()) {
					if (widget.contains(mx, my) && !this.isInteractionBlocked(widget, i, screens)) {
						if (this.processMouseEvent(widget, button, eventType, mx, my).consume(false)) {
							return;
						}
					}
				}
				if (this.processMouseEvent(screen, button, eventType, mx, my).consume(false)) {
					return;
				}
			}
			if (screen.isFullScreen()) {
				return;
			}
		}
	}

	private EventResult processMouseEvent(final GuiBase receiver, final int button, final MouseEventType eventType, final int mouseX, final int mouseY) {
		return switch (eventType) {
			case RELEASE -> receiver.onMouseReleased(button, mouseX, mouseY);
			case PRESS -> receiver.onMousePressed(button, mouseX, mouseY);
			case CLICK -> receiver.onMouseClicked(button, mouseX, mouseY);
		};
	}

	private List<GuiScreen> copyStack() {
		return new ArrayList<>(this.screens);
	}
}
