package dawn.gfx;

import dawn.input.Input;

public interface Window {

	long getPointer();

	int getWidth();

	int getHeight();

	int getWindowWidth();

	int getWindowHeight();

	int getFramebufferWidth();

	int getFramebufferHeight();

	Input getInput();
}
