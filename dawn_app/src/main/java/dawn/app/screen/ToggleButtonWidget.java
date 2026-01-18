package dawn.app.screen;

import java.util.function.BooleanSupplier;
import dawn.gfx.Color;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gui.GuiWidget;
import dawn.gui.Interactivity;
import dawn.init.Textures;
import dawn.input.EventResult;
import dawn.input.Input;
import dawn.lib.BooleanConsumer;
import dawn.lib.option.ManagedBoolean;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

public class ToggleButtonWidget extends GuiWidget {

	@Getter
	@Setter
	private @Nullable String label;
	private final BooleanSupplier getter;
	private final BooleanConsumer setter;

	public ToggleButtonWidget(@Nullable final String label, final BooleanSupplier getter, final BooleanConsumer setter) {
		this.label = label;
		this.getter = getter;
		this.setter = setter;
	}

	public ToggleButtonWidget(@Nullable final String label, final ManagedBoolean state) {
		this(label, state, state);
	}

	@Override
	public void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
		final int mx = (int) input.getMouseX();
		final int my = (int) input.getMouseY();
		tessellator.start()
			.set(this.getter.getAsBoolean() ? Textures.GUI_BUTTON_PRESSED : Textures.GUI_BUTTON)
			.pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw();
		if (interactivity == Interactivity.INTERACTIVE && this.contains(mx, my)) {
			tessellator.set(Color.WHITE.withAlpha(.1f)).pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw();
		}
		tessellator.end();
	}

	@Override
	public void renderForeground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
		if (this.label != null) {
			final int fontSize = Math.min(this.getHeight() / 4 * 3, 16);
			final int w = fontRenderer.getStringWidth(this.label, fontSize);
			final int h = fontRenderer.getStringHeight(fontSize);
			final int yOffset = this.getter.getAsBoolean() ? 1 : 0;
			fontRenderer.start().set(this.label).size(fontSize).pos(this.getX() + (this.getWidth() - w) / 2, this.getY() + (this.getHeight() - h) / 2 - 1 - yOffset).write().end();
		}
	}

	@Override
	public EventResult onMouseClicked(final int button, final int mouseX, final int mouseY) {
		final boolean current = this.getter.getAsBoolean();
		this.setter.accept(!current);
		return EventResult.CONSUME;
	}
}
