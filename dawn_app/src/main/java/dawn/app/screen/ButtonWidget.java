package dawn.app.screen;

import dawn.gfx.Color;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gui.GuiWidget;
import dawn.gui.RenderPhase;
import dawn.init.Textures;
import dawn.input.EventResult;
import dawn.input.Input;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

public class ButtonWidget extends GuiWidget {

	@Getter
	@Setter
	private @Nullable String label;
	private final Runnable callback;

	public ButtonWidget(@Nullable final String label, final Runnable callback) {
		this.label = label;
		this.callback = callback;
	}

	@Override
	public void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final RenderPhase phase, final Input input) {
		final int mx = (int) input.getMouseX();
		final int my = (int) input.getMouseY();
		switch (phase) {
			case BACKGROUND -> tessellator.start().set(Textures.GUI_BUTTON).pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw().end();
			case INTERACTIVE -> {
				tessellator.start();
				if (input.isMouseDownLeft() && this.contains(mx, my)) {
					tessellator.set(Textures.GUI_BUTTON_PRESSED).pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw();
				} else if (this.contains(mx, my)) {
					tessellator.set(Color.WHITE.withAlpha(.1f)).pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw();
				}
				tessellator.end();
			}
		}
		if (this.label != null) {
			final int fontSize = Math.min(this.getHeight() / 4 * 3, 16);
			final int w = fontRenderer.getStringWidth(this.label, fontSize);
			final int h = fontRenderer.getStringHeight(fontSize);
			final int yOffset = this.contains(mx, my) && input.isMouseDownLeft() ? 0 : 1;
			fontRenderer.start().set(this.label).size(fontSize).pos(this.getX() + (this.getWidth() - w) / 2, this.getY() + (this.getHeight() - h) / 2 - 1 - yOffset).write().end();
		}
	}

	@Override
	public EventResult onMouseClicked(final int button, final int mouseX, final int mouseY) {
		this.callback.run();
		return EventResult.CONSUME;
	}
}
