package dawn.app.screen;

import dawn.Dawn;
import dawn.Identifier;
import dawn.app.DawnApp;
import dawn.app.init.AppTextures;
import dawn.core.DawnImpl;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gui.GuiScreen;
import dawn.gui.Interactivity;
import dawn.gui.widget.ButtonWidget;
import dawn.input.Input;
import dawn.lib.lang.Text;

public final class ScreenMainMenu extends GuiScreen {

	private static final Identifier SCREEN_ID = Identifier.of("main_menu");
	private final ButtonWidget buttonOptions = this.add(new ButtonWidget(Text.localize(Dawn.makeKey(ScreenMainMenu.SCREEN_ID, "screen", "options")), this::onButtonOptions));
	private final ButtonWidget buttonExit = this.add(new ButtonWidget(Text.localize(Dawn.makeKey(ScreenMainMenu.SCREEN_ID, "screen", "exit"), DawnImpl.NAME), this::onButtonExit));

	public ScreenMainMenu() {
		this.buttonExit.setPos(() -> this.getX() + this.getWidth() / 2 - 120, () -> this.getY() + this.getHeight() - 24 - 10);
		this.buttonOptions.setPos(() -> this.getX() + this.getWidth() / 2 - 120, () -> this.buttonExit.getY() - 24 - 10);
	}

	@Override
	public void init() {
		this.setFullscreen();
		super.init();
	}

	@Override
	public void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
		tessellator
			.render(AppTextures.ARGON, t -> t.pos(this.getBounds()))
			.start().set(AppTextures.LOGO).pos(this.getX(), this.getY() + 10).centerX(this.getWidth()).draw().end();
	}

	private void onButtonOptions() {
		this.getGuiManager().openScreen(new ScreenOptions());
	}

	private void onButtonExit() {
		DawnApp.get().stop();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
