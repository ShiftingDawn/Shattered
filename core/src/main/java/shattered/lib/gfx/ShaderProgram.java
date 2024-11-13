package shattered.lib.gfx;

import java.io.FileNotFoundException;
import java.io.IOException;
import shattered.FileUtils;
import shattered.core.ExitShatteredException;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import static shattered.Shattered.LOGGER;

public final class ShaderProgram {

	private static final String VERSION = "330 core";
	private final int program;

	public ShaderProgram(final String vertPath, final String fragPath, final String outputColorName) {
		//Generate ids
		final int vertShaderId = glCreateShader(GL_VERTEX_SHADER);
		final int fragShaderId = glCreateShader(GL_FRAGMENT_SHADER);
		this.program = glCreateProgram();
		//Load and compile shaders
		ShaderProgram.compileShader(vertShaderId, vertPath);
		ShaderProgram.compileShader(fragShaderId, fragPath);
		//Configure shaders and program
		glAttachShader(this.program, fragShaderId);
		glAttachShader(this.program, vertShaderId);
		glBindFragDataLocation(fragShaderId, 0, outputColorName);
		glLinkProgram(this.program);
		if (glGetProgrami(this.program, GL_LINK_STATUS) != GL_TRUE) {
			throw new ExitShatteredException("Could not link GL Program");
		}
		glDeleteShader(vertShaderId);
		glDeleteShader(fragShaderId);
	}

	public void bind() {
		glUseProgram(this.program);
	}

	public static void unbind() {
		glUseProgram(0);
	}

	public void destroy() {
		glDeleteShader(this.program);
	}

	private static void compileShader(final int shaderId, final String path) {
		try {
			final String content = FileUtils.readFromClassPath(path);
			if (content == null) {
				throw new FileNotFoundException("Could not load shader file: " + path);
			}
			glShaderSource(shaderId, "#version %s\n\n".formatted(ShaderProgram.VERSION) + content);
			glCompileShader(shaderId);
			if (glGetShaderi(shaderId, GL_COMPILE_STATUS) != GL_TRUE) {
				throw new ExitShatteredException("Could not compile shader: " + path);
			}
		} catch (final IOException e) {
			LOGGER.error(e);
			throw new ExitShatteredException("Could not compile shader: " + path);
		}
	}
}
