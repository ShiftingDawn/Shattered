package dawn.gfx;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

public final class Display {

	@Setter(AccessLevel.PACKAGE)
	private static @Getter long window;
	@Setter(AccessLevel.PACKAGE)
	private static @Getter int width;
	@Setter(AccessLevel.PACKAGE)
	private static @Getter int height;

	public static void activate() {
		glfwMakeContextCurrent(Display.window);
		glfwSwapInterval(1);
		GL.createCapabilities();
	}

	private Display() {
	}
}
