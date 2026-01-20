package dawn.gui;

import dawn.Identifier;
import dawn.gfx.Window;

public interface GuiManager {

	void openScreen(GuiScreen screen);

	void closeScreen(GuiScreen screen);

	void playAudio(Identifier sound);

	Window getWindow();
}
