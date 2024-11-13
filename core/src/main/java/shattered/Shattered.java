package shattered;

import java.io.File;
import java.io.IOException;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import shattered.core.event.EventBusImpl;
import shattered.lib.Internal;
import shattered.lib.gfx.BufferBuilder;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.GeneralVertexFormats;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

public final class Shattered {

	public static final String NAME = "Shattered";
	public static final Logger LOGGER = LogManager.getLogger(Shattered.NAME);
	@Getter
	private static Shattered shattered;

	private Shattered(final File rootDir, final String[] args) {
		Shattered.shattered = this;
		Internal.NAME = Shattered.NAME;
		Internal.ROOT_PATH = rootDir.toPath().toAbsolutePath();
		//TODO handle args
		this.init();
		try {
			this.start();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void init() {
		EventBusImpl.init();
		Display.init();
	}

	private void start() throws IOException {
		glfwMakeContextCurrent(Display.getWindow());
		glfwSwapInterval(0);
		GL.createCapabilities();

		glClearColor(0.6f, 0.7f, 0.8f, 1.0f);

		// Create a simple shader program
		final int program = glCreateProgram();
		final int vs = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vs, FileUtils.readFromClassPath("/root.vert"));
		glCompileShader(vs);
		glAttachShader(program, vs);
		final int fs = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fs, FileUtils.readFromClassPath("/root.frag"));
		glCompileShader(fs);
		glAttachShader(program, fs);
		glBindFragDataLocation(fs, 0, "outColor");
		glLinkProgram(program);
		glUseProgram(program);

		while (!GLFW.glfwWindowShouldClose(Display.getWindow())) {
			glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
			glViewport(0, 0, Display.getWidth(), Display.getHeight());
			final BufferBuilder b = new BufferBuilder(GeneralVertexFormats.FORMAT_COLOR, 4, GL_TRIANGLE_FAN, () -> {
			});
			b.position(-0.5f, -0.5f).color(1f, 1f, 0f, 1f).endVertex();
			b.position(0.5f, -0.5f).color(1f, 1f, 0f, 1f).endVertex();
			b.position(0.5f, 0.5f).color(1f, 1f, 0f, 1f).endVertex();
			b.position(-0.5f, 0.5f).color(1f, 1f, 0f, 1f).endVertex();
			b.draw();

			glfwSwapBuffers(Display.getWindow());
			glfwPollEvents();
		}
	}

	public void stop() {
		Shattered.LOGGER.warn("{} will now exit", Shattered.NAME);
	}

	public static void start(final File rootDir, final String[] args) {
		if (Shattered.getShattered() != null) {
			throw new IllegalStateException("The start-method can only be called once!");
		}
		new Shattered(rootDir, args);
	}
}
