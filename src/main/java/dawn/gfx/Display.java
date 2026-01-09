package dawn.gfx;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

public final class Display {

	public static final int DEFAULT_WIDTH = 800;
	public static final int DEFAULT_HEIGHT = 600;

	@Setter(AccessLevel.PACKAGE)
	private static @Getter long window;
	@Setter(AccessLevel.PACKAGE)
	private static @Getter int width = DEFAULT_WIDTH;
	@Setter(AccessLevel.PACKAGE)
	private static @Getter int height = DEFAULT_HEIGHT;

	public static void activate() {
		glfwMakeContextCurrent(getWindow());
		glfwSwapInterval(1);
		GL.createCapabilities();
	}

	private Display() {
	}
}
