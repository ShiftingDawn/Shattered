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
import shattered.lib.gfx.ShaderProgram;
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

		final ShaderProgram shader = new ShaderProgram("/root.vert", "/root.frag", "outColor");
		shader.bind();

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
