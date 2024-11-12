package shattered;

import java.io.File;
import java.nio.FloatBuffer;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import shattered.core.event.EventBusImpl;
import shattered.lib.Internal;
import shattered.lib.gfx.Display;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_LINE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPolygonOffset;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

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
		this.start();
	}

	private void init() {
		EventBusImpl.init();
		Display.init();
	}

	Matrix4f viewProjMatrix = new Matrix4f();
	FloatBuffer fb = BufferUtils.createFloatBuffer(16);

	private void start() {
		glfwMakeContextCurrent(Display.getWindow());
		glfwSwapInterval(0);
		GL.createCapabilities();

		glClearColor(0.6f, 0.7f, 0.8f, 1.0f);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		// Create a simple shader program
		final int program = glCreateProgram();
		final int vs = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vs, """
				uniform mat4 viewProjMatrix;
				void main(void) {
				    gl_Position = viewProjMatrix * gl_Vertex;
				}
				""");
		glCompileShader(vs);
		glAttachShader(program, vs);
		final int fs = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fs, """
				uniform vec3 color;
				void main(void) {
					gl_FragColor = vec4(color, 1.0);
				}
				""");
		glCompileShader(fs);
		glAttachShader(program, fs);
		glLinkProgram(program);
		glUseProgram(program);

		// Obtain uniform location
		final int matLocation = glGetUniformLocation(program, "viewProjMatrix");
		final int colorLocation = glGetUniformLocation(program, "color");
		long lastTime = System.nanoTime();

		/* Quaternion to rotate the cube */
		final Quaternionf q = new Quaternionf();

		while (!GLFW.glfwWindowShouldClose(Display.getWindow())) {
			final long thisTime = System.nanoTime();
			final float dt = (thisTime - lastTime) / 1E9f;
			lastTime = thisTime;

			glViewport(0, 0, Display.getWidth(), Display.getHeight());
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			// Create a view-projection matrix
			this.viewProjMatrix.setPerspective((float) Math.toRadians(45.0f),
							(float) Display.getWidth() / Display.getHeight(), 0.01f, 100.0f)
					.lookAt(0.0f, 4.0f, 10.0f,
							0.0f, 0.5f, 0.0f,
							0.0f, 1.0f, 0.0f);
			// Upload the matrix stored in the FloatBuffer to the
			// shader uniform.
			glUniformMatrix4fv(matLocation, false, this.viewProjMatrix.get(this.fb));
			// Render the grid without rotating
			glUniform3f(colorLocation, 0.3f, 0.3f, 0.3f);
			this.renderGrid();

			// rotate the cube (45 degrees per second)
			// and translate it by 0.5 in y
			this.viewProjMatrix.translate(0.0f, 0.5f, 0.0f)
					.rotate(q.rotateY((float) Math.toRadians(45) * dt).normalize());
			// Upload the matrix
			glUniformMatrix4fv(matLocation, false, this.viewProjMatrix.get(this.fb));

			// Render solid cube with outlines
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			glUniform3f(colorLocation, 0.6f, 0.7f, 0.8f);
			this.renderCube();
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			glEnable(GL_POLYGON_OFFSET_LINE);
			glPolygonOffset(-1.f, -1.f);
			glUniform3f(colorLocation, 0.0f, 0.0f, 0.0f);
			this.renderCube();
			glDisable(GL_POLYGON_OFFSET_LINE);

			glfwSwapBuffers(Display.getWindow());
			glfwPollEvents();
		}
	}

	void renderCube() {
		glBegin(GL_QUADS);
		glVertex3f(0.5f, -0.5f, -0.5f);
		glVertex3f(-0.5f, -0.5f, -0.5f);
		glVertex3f(-0.5f, 0.5f, -0.5f);
		glVertex3f(0.5f, 0.5f, -0.5f);

		glVertex3f(0.5f, -0.5f, 0.5f);
		glVertex3f(0.5f, 0.5f, 0.5f);
		glVertex3f(-0.5f, 0.5f, 0.5f);
		glVertex3f(-0.5f, -0.5f, 0.5f);

		glVertex3f(0.5f, -0.5f, -0.5f);
		glVertex3f(0.5f, 0.5f, -0.5f);
		glVertex3f(0.5f, 0.5f, 0.5f);
		glVertex3f(0.5f, -0.5f, 0.5f);

		glVertex3f(-0.5f, -0.5f, 0.5f);
		glVertex3f(-0.5f, 0.5f, 0.5f);
		glVertex3f(-0.5f, 0.5f, -0.5f);
		glVertex3f(-0.5f, -0.5f, -0.5f);

		glVertex3f(0.5f, 0.5f, 0.5f);
		glVertex3f(0.5f, 0.5f, -0.5f);
		glVertex3f(-0.5f, 0.5f, -0.5f);
		glVertex3f(-0.5f, 0.5f, 0.5f);

		glVertex3f(0.5f, -0.5f, -0.5f);
		glVertex3f(0.5f, -0.5f, 0.5f);
		glVertex3f(-0.5f, -0.5f, 0.5f);
		glVertex3f(-0.5f, -0.5f, -0.5f);
		glEnd();
	}

	void renderGrid() {
		glBegin(GL_LINES);
		for (int i = -20; i <= 20; i++) {
			glVertex3f(-20.0f, 0.0f, i);
			glVertex3f(20.0f, 0.0f, i);
			glVertex3f(i, 0.0f, -20.0f);
			glVertex3f(i, 0.0f, 20.0f);
		}
		glEnd();
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
