package shattered;

import java.io.File;
import java.io.IOException;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import shattered.core.event.EventBusImpl;
import shattered.lib.Internal;
import shattered.lib.gfx.BufferBuilder;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.GeneralVertexFormats;
import shattered.lib.gfx.MatrixUtils;
import shattered.lib.gfx.ShaderProgram;
import shattered.lib.gfx.ShaderProps;
import shattered.lib.gfx.Texture;
import shattered.lib.gfx.TextureLoader;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glBindTexture;
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

		final Texture t1 = TextureLoader.loadTexture("/argon.png");

		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(shader, "projectionMatrix"), false, MatrixUtils.ortho());
		ShaderProps.setUniform4(ShaderProps.getNamedLocation(shader, "modelViewMatrix"), false, new Matrix4f().identity());

		glBindTexture(GL_TEXTURE_2D, t1.getId());

		while (!GLFW.glfwWindowShouldClose(Display.getWindow())) {
			glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
			final BufferBuilder b = new BufferBuilder(GeneralVertexFormats.FORMAT_TEXTURE, 4, GL_TRIANGLE_FAN, () -> {
				ShaderProps.setUniform1(ShaderProps.getNamedLocation(shader, "enableTextures"), 1);
			});
			b.position(0, 0).color(1, 1, 1, 1).uv(0, 0).endVertex();
			b.position(Display.getWidth(), 0).color(1, 1, 1, 1).uv(1, 0).endVertex();
			b.position(Display.getWidth(), Display.getHeight()).color(1, 1, 1, 1).uv(1, 1).endVertex();
			b.position(0, Display.getHeight()).color(1, 1, 1, 1).uv(0, 1).endVertex();

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
