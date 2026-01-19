package dawn.app.screen;

import dawn.Dawn;
import dawn.Identifier;
import dawn.app.init.AppTextures;
import dawn.gfx.Color;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gui.GuiScreen;
import dawn.gui.Interactivity;
import dawn.gui.widget.ButtonWidget;
import dawn.input.Input;
import dawn.lib.Rectangle;
import dawn.lib.lang.Text;
import lombok.Getter;

abstract class OverlayWindow extends GuiScreen {

	private final ButtonWidget buttonClose = this.add(new ButtonWidget(Text.localize(Dawn.makeKey(Identifier.of("generic"), "screen", "close")), this::onButtonClose));
	private final Text title;
	private @Getter Rectangle innerBounds;

	public OverlayWindow(final Text title) {
		this.title = title;
		this.buttonClose.setSize(32, 16);
		this.buttonClose.setPos(() -> this.getX() + 2, () -> this.getY() + 2);
	}

	@Override
	public void init() {
		this.setSize(Math.min(this.getDisplayWidth(), 340), Math.min(this.getDisplayHeight(), 220));
		super.init();
		this.innerBounds = new Rectangle(this.getX() + 2, this.getY() + 20, this.getWidth() - 4, this.getHeight() - 22);
	}

	protected void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input, final Rectangle bounds) {
	}

	protected void renderForeground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input, final Rectangle bounds) {
	}

	@Override
	public final void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
		tessellator
			.render(Color.BLACK, t -> t.pos(this.getX() + 3, this.getY() + 3, this.getWidth(), this.getHeight()))
			.render(AppTextures.GUI_BACKGROUND_TITLE, t -> t.pos(this.getX(), this.getY(), this.getWidth(), 18))
			.render(AppTextures.GUI_BACKGROUND, t -> t.pos(this.getX(), this.getY() + 18, this.getWidth(), this.getHeight() - 18));
		final int w = fontRenderer.getStringWidth(this.title, 16);
		final int h = fontRenderer.getStringHeight(16);
		fontRenderer.start().size(16).set(this.title).pos(this.getX() + (this.getWidth() - w) / 2, this.getY() + 1 + (16 - h) / 2).write().end();
		this.renderBackground(tessellator, fontRenderer, interactivity, input, this.innerBounds);
	}

	@Override
	public void renderForeground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
		this.renderForeground(tessellator, fontRenderer, interactivity, input, this.innerBounds);
	}

	private void onButtonClose() {
		this.getGuiManager().closeScreen(this);
	}
}
