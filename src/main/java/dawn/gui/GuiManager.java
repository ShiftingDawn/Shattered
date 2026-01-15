package dawn.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import dawn.event.EventBus;
import dawn.gfx.DisplayResizedEvent;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import org.lwjgl.glfw.GLFW;

public final class GuiManager {

	private final List<GuiScreen> screens = new ArrayList<>();

	public GuiManager() {
		EventBus.register(DisplayResizedEvent.class, _ -> this.reload());
	}

	public void openScreen(final GuiScreen screen) {
		if (this.screens.contains(screen)) {
			//Move to top
			this.screens.remove(screen);
			this.screens.addLast(screen);
			return;
		}
		this.screens.addLast(screen);
		screen.init();
	}

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

	public void render(final Tessellator tessellator, final FontRenderer fontRenderer, final int mouseX, final int mouseY) {
		this.walkStack(true, screen -> {
			screen.renderBackground(tessellator, fontRenderer, mouseX, mouseY);
			for (final GuiWidget widget : screen.getWidgets()) {
				widget.renderBackground(tessellator, fontRenderer, mouseX, mouseY);
			}
			screen.renderForeground(tessellator, fontRenderer, mouseX, mouseY);
			for (final GuiWidget widget : screen.getWidgets()) {
				widget.renderForeground(tessellator, fontRenderer, mouseX, mouseY);
			}
		});
	}

	public void handleMouseEvent(final int button, final int action, final double mouseX, final double mouseY) {
		final int mx = (int) mouseX;
		final int my = (int) mouseY;
		this.walkStack(true, screen -> {
			if (screen.contains(mx, my)) {
				if (this.processMouseEvent(screen, button, action, mx, my)) {
					return true;
				}
				for (final GuiWidget widget : screen.getWidgets()) {
					if (widget.contains(mx, my) && this.processMouseEvent(widget, button, action, mx, my)) {
						return true;
					}
				}
			}
			return false;
		});

	}

	private boolean processMouseEvent(final GuiBase receiver, final int button, final int action, final int mouseX, final int mouseY) {
		return switch (action) {
			case -1 -> receiver.onMouseClicked(button, mouseX, mouseY);
			case GLFW.GLFW_PRESS -> receiver.onMousePressed(button, mouseX, mouseY);
			case GLFW.GLFW_RELEASE -> receiver.onMouseReleased(button, mouseX, mouseY);
			default -> throw new IllegalArgumentException("Invalid action type: " + action);
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
