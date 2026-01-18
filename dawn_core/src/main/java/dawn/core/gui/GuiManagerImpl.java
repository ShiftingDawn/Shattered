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
import dawn.gui.Interactivity;
import dawn.input.EventResult;
import dawn.input.Input;
import dawn.input.KeyEventType;
import dawn.input.KeyMods;
import dawn.input.KeyPressedEvent;
import dawn.input.KeyReleasedEvent;
import dawn.input.KeyRepeatEvent;
import dawn.input.MouseClickEvent;
import dawn.input.MouseEventType;
import dawn.input.MousePressedEvent;
import dawn.input.MouseReleasedEvent;
import dawn.internal.DawnLib;
import dawn.lib.Rectangle;
import lombok.Getter;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public final class GuiManagerImpl implements GuiManager {

	private final List<GuiScreen> screens = new ArrayList<>();
	private final @Getter Window window;

	public GuiManagerImpl(final Window window) {
		this.window = window;
		EventBus.bus().register(MouseReleasedEvent.class, true, this, this::handleMouseReleased);
		EventBus.bus().register(MousePressedEvent.class, true, this, this::handleMousePressed);
		EventBus.bus().register(MouseClickEvent.class, true, this, this::handleMouseClicked);
		EventBus.bus().register(KeyReleasedEvent.class, true, this, this::handleKeyReleased);
		EventBus.bus().register(KeyPressedEvent.class, true, this, this::handleKeyPressed);
		EventBus.bus().register(KeyRepeatEvent.class, true, this, this::handleKeyRepeat);
		EventBus.bus().register(WindowResizedEvent.class, e -> {
			if (e.pointer() == window.getPointer()) {
				this.reload();
			}
		});
	}

	@Override
	public void openScreen(final GuiScreen screen) {
		if (this.screens.contains(screen)) {
			this.screens.remove(screen); //Move to top
			this.screens.addLast(screen);
			return;
		}
		if (!screen.allowMultipleInstances()) {
			for (final GuiScreen existing : this.screens) {
				if (existing.getClass() == screen.getClass()) {
					return;
				}
			}
		}
		DawnLib.GUI_MANAGER.set(this);
		screen.getGuiManager(); //Force load the field
		this.screens.addLast(screen);
		screen.init();
	}

	@Override
	public void closeScreen(final GuiScreen screen) {
		if (this.screens.size() > 1) {
			this.screens.remove(screen);
		}
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
		final Input input = this.window.getInput();
		for (int i = renderStartIndex; i < screens.size(); ++i) {
			final GuiScreen screen = screens.get(i);
			final boolean interactionBlocked = this.isInteractionBlocked(screen, i, screens);
			screen.renderBackground(tessellator, fontRenderer, interactionBlocked ? Interactivity.BLOCKED : Interactivity.INTERACTIVE, input);
			for (final GuiWidget widget : screen.getWidgets()) {
				widget.renderBackground(tessellator, fontRenderer, this.isInteractionBlocked(widget, i, screens) ? Interactivity.BLOCKED : Interactivity.INTERACTIVE, input);
			}
			screen.renderForeground(tessellator, fontRenderer, interactionBlocked ? Interactivity.BLOCKED : Interactivity.INTERACTIVE, input);
			for (final GuiWidget widget : screen.getWidgets()) {
				widget.renderForeground(tessellator, fontRenderer, this.isInteractionBlocked(widget, i, screens) ? Interactivity.BLOCKED : Interactivity.INTERACTIVE, input);
			}
		}
	}

	private void handleMouseReleased(final MouseReleasedEvent event) {
		this.handleMouseEvent(event.getButton(), MouseEventType.RELEASE, event.getXPos(), event.getYPos());
	}

	private void handleMousePressed(final MousePressedEvent event) {
		this.handleMouseEvent(event.getButton(), MouseEventType.PRESS, event.getXPos(), event.getYPos());
	}

	private void handleMouseClicked(final MouseClickEvent event) {
		this.handleMouseEvent(event.getButton(), MouseEventType.CLICK, event.getXPos(), event.getYPos());
	}

	private void handleMouseEvent(final int button, final MouseEventType eventType, final double mouseX, final double mouseY) {
		final List<GuiScreen> screens = this.copyStack();
		final int mx = (int) mouseX;
		final int my = (int) mouseY;
		for (int i = screens.size() - 1; i >= 0; --i) {
			final GuiScreen screen = screens.get(i);
			if (screen.contains(mx, my) && !this.isInteractionBlocked(screen, i, screens)) {
				for (final GuiWidget widget : screen.getWidgets()) {
					if (!widget.contains(mx, my)) {
						continue;
					}
					if (this.isInteractionBlocked(widget, i, screens)) {
						continue;
					}
					if (this.processMouseEvent(widget, button, eventType, mx, my).consume(false)) {
						return;
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

	private void handleKeyReleased(final KeyReleasedEvent event) {
		this.handleKeyEvent(event.getKey(), KeyEventType.RELEASE, event.getMods());
	}

	private void handleKeyPressed(final KeyPressedEvent event) {
		this.handleKeyEvent(event.getKey(), KeyEventType.PRESS, event.getMods());
	}

	private void handleKeyRepeat(final KeyRepeatEvent event) {
		this.handleKeyEvent(event.getKey(), KeyEventType.REPEAT, event.getMods());
	}

	private void handleKeyEvent(final int keyCode, final KeyEventType eventType, final KeyMods mods) {
		final List<GuiScreen> screens = this.copyStack();
		if (eventType == KeyEventType.RELEASE && keyCode == GLFW_KEY_ESCAPE) {
			final GuiScreen last = screens.getLast();
			if (last.shouldCloseOnEsc()) {
				this.closeScreen(last);
			}
		}
		for (int i = screens.size() - 1; i >= 0; --i) {
			final GuiScreen screen = screens.get(i);
			if (!this.isInteractionBlocked(screen, i, screens)) {
				for (final GuiWidget widget : screen.getWidgets()) {
					if (this.isInteractionBlocked(widget, i, screens)) {
						continue;
					}
					if (this.processKeyEvent(widget, keyCode, eventType, mods).consume(false)) {
						return;
					}
				}
				if (this.processKeyEvent(screen, keyCode, eventType, mods).consume(false)) {
					return;
				}
			}
			if (screen.isFullScreen()) {
				return;
			}
		}
	}

	private EventResult processKeyEvent(final GuiBase receiver, final int keyCode, final KeyEventType eventType, final KeyMods mods) {
		return switch (eventType) {
			case RELEASE -> receiver.onKeyReleased(keyCode, mods);
			case PRESS -> receiver.onKeyPressed(keyCode, mods);
			case REPEAT -> receiver.onKeyRepeat(keyCode, mods);
		};
	}

	private List<GuiScreen> copyStack() {
		return new ArrayList<>(this.screens);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted") //It's not
	private boolean isInteractionBlocked(final GuiWidget widget, final int currentScreen, final List<GuiScreen> screenStack) {
		final Rectangle widgetBounds = widget.getBounds();
		for (int i = screenStack.size() - 1; i > currentScreen; --i) {
			final GuiScreen screen = screenStack.get(i);
			if (screen.isBlockingInteractionBelow() && screen.getBounds().intersects(widgetBounds)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted") //It's not
	private boolean isInteractionBlocked(final GuiScreen screen, final int currentScreen, final List<GuiScreen> screenStack) {
		final Rectangle screenBounds = screen.getBounds();
		for (int i = screenStack.size() - 1; i > currentScreen; --i) {
			final GuiScreen otherScreen = screenStack.get(i);
			if (otherScreen.isBlockingInteractionBelow() && otherScreen.getBounds().contains(screenBounds)) {
				return true;
			}
		}
		return false;
	}
}
