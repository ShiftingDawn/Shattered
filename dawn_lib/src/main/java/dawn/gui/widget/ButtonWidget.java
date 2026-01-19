package dawn.gui.widget;

import dawn.gfx.Color;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gui.GuiWidget;
import dawn.gui.Interactivity;
import dawn.init.Textures;
import dawn.input.EventResult;
import dawn.input.Input;
import dawn.lib.lang.Text;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

public class ButtonWidget extends GuiWidget {

	@Getter
	@Setter
	private @Nullable Text label;
	private final Runnable callback;

	public ButtonWidget(@Nullable final Text label, final Runnable callback) {
		this.label = label;
		this.callback = callback;
		this.setSize(240, 24);
	}

	public ButtonWidget(@Nullable final String label, final Runnable callback) {
		this(label != null ? Text.literal(label) : null, callback);
	}

	@Override
	public void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
		final int mx = (int) input.getMouseX();
		final int my = (int) input.getMouseY();
		tessellator.start();
		if (interactivity == Interactivity.BLOCKED || !this.contains(mx, my) || !input.isMouseDownLeft()) {
			tessellator.set(Textures.GUI_BUTTON).pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw();
		}
		if (interactivity == Interactivity.INTERACTIVE && this.contains(mx, my)) {
			if (input.isMouseDownLeft()) {
				tessellator.set(Textures.GUI_BUTTON_PRESSED).pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw();
			} else {
				tessellator.set(Color.WHITE.withAlpha(.1f)).pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw();
			}
		}
		tessellator.end();
	}

	@Override
	public void renderForeground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
		if (this.label != null) {
			final int mx = (int) input.getMouseX();
			final int my = (int) input.getMouseY();
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
