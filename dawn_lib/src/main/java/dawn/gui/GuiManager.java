package dawn.gui;

import dawn.gfx.Window;

public interface GuiManager {

	void openScreen(GuiScreen screen);

	void closeScreen(GuiScreen screen);

	Window getWindow();
}
