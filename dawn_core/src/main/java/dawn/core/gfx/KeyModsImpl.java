package dawn.core.gfx;

import dawn.input.KeyMods;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SUPER;

record KeyModsImpl(boolean shift, boolean ctrl, boolean alt, boolean meta) implements KeyMods {

	public static KeyMods of(final int mods) {
		return new KeyModsImpl(
			(mods & GLFW_MOD_SHIFT) == GLFW_MOD_SHIFT,
			(mods & GLFW_MOD_CONTROL) == GLFW_MOD_CONTROL,
			(mods & GLFW_MOD_ALT) == GLFW_MOD_ALT,
			(mods & GLFW_MOD_SUPER) == GLFW_MOD_SUPER
		);
	}
}
