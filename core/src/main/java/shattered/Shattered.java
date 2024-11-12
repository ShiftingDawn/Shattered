package shattered;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import shattered.core.event.EventBusImpl;
import shattered.lib.Internal;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.VertexArrayObject;
import shattered.lib.gfx.VertexBufferObject;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
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
		this.start();
	}

	private void init() {
		EventBusImpl.init();
		Display.init();
	}

	private void start() {
		glfwMakeContextCurrent(Display.getWindow());
		glfwSwapInterval(0);
		GL.createCapabilities();

		glClearColor(0.6f, 0.7f, 0.8f, 1.0f);

		// Create a simple shader program
		final int program = glCreateProgram();
		final int vs = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vs, """
				#version 150 core
				
				in vec2 position;
				
				void main(void) {
					gl_Position = vec4(position, 0.0, 1.0);
				}
				""");
		glCompileShader(vs);
		glAttachShader(program, vs);
		final int fs = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fs, """
				#version 150 core
				
				 uniform vec3 cols[4];
				 uniform int chosen;
				
				 out vec4 color;
				
				 void main(void) {
				 	color = vec4(cols[chosen], 1.0);
				 }
				""");
		glCompileShader(fs);
		glAttachShader(program, fs);
		glBindAttribLocation(program, 0, "position");
		glBindFragDataLocation(program, 0, "color");
		glLinkProgram(program);
		glUseProgram(program);

		final int vec3ArrayUniform;
		final int chosenUniform;
		int chosen = 0;

		vec3ArrayUniform = glGetUniformLocation(program, "cols");
		chosenUniform = glGetUniformLocation(program, "chosen");

		final VertexArrayObject vao = new VertexArrayObject();
		final VertexBufferObject vbo = new VertexBufferObject();

		final ByteBuffer bb = BufferUtils.createByteBuffer(4 * 2 * 6);
		final FloatBuffer fv = bb.asFloatBuffer();
		fv.put(-1.0f).put(-1.0f);
		fv.put(1.0f).put(-1.0f);
		fv.put(1.0f).put(1.0f);
		fv.put(1.0f).put(1.0f);
		fv.put(-1.0f).put(1.0f);
		fv.put(-1.0f).put(-1.0f);
		vbo.uploadData(bb, GL_STATIC_DRAW);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0L);
		vbo.unbind();

		final FloatBuffer colors = BufferUtils.createFloatBuffer(3 * 4);
		{
			colors.put(1).put(0).put(0); // red
			colors.put(0).put(1).put(0); // green
			colors.put(0).put(0).put(1); // blue
			colors.put(1).put(1).put(0); // yellow
			colors.flip();
		}

		while (!GLFW.glfwWindowShouldClose(Display.getWindow())) {
			chosen = (int) ((System.currentTimeMillis() / 1000) % 4);
			glClear(GL_COLOR_BUFFER_BIT);

			glUniform3fv(vec3ArrayUniform, colors);
			/* Set chosen color (index into array) */
			glUniform1i(chosenUniform, chosen);
			glDrawArrays(GL_TRIANGLES, 0, 6);

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
