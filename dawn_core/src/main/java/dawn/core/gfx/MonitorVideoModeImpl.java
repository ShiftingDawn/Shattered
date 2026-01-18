package dawn.core.gfx;

import dawn.gfx.MonitorVideoMode;
import org.lwjgl.glfw.GLFWVidMode;

public record MonitorVideoModeImpl(int width, int height, int refreshRate, int redBits, int greenBits, int blueBits) implements MonitorVideoMode {

	@Override
	public String toString() {
		return this.width + "x" + this.height + "@" + this.refreshRate;
	}

	public static MonitorVideoModeImpl of(final GLFWVidMode mode) {
		return new MonitorVideoModeImpl(mode.width(), mode.height(), mode.refreshRate(), mode.redBits(), mode.greenBits(), mode.blueBits());
	}
}