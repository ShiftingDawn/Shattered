package dawn.gfx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import dawn.asset.AssetResolver;
import dawn.lib.ExitException;
import dawn.registry.Identifier;
import lombok.Getter;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

public final class Shader {

	private final @Getter int program;

	public Shader(final AssetResolver assets, final String path, final String outputColorName) {
		//Generate shaders and program
		final int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		final int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		this.program = glCreateProgram();
		//Load and compile shaders
		Shader.compileShader(vertexShader, assets, assets.makePath(Identifier.of(path), null, "vert"));
		Shader.compileShader(fragmentShader, assets, assets.makePath(Identifier.of(path), null, "frag"));
		//Configure shaders and program
		glAttachShader(this.program, fragmentShader);
		glAttachShader(this.program, vertexShader);
		glBindFragDataLocation(fragmentShader, 0, outputColorName);
		glLinkProgram(this.program);
		if (glGetProgrami(this.program, GL_LINK_STATUS) != GL_TRUE) {
			GlfwSetup.LOGGER.fatal("Could not link shader program");
			throw new ExitException();
		}
		//Delete shaders
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	public void bind() {
		glUseProgram(this.program);
	}

	public void unbind() {
		glUseProgram(0);
	}

	public void destroy() {
		glDeleteProgram(this.program);
	}

	private static void compileShader(final int shader, final AssetResolver assets, final String shaderPath) {
		try {
			final InputStream stream = assets.getStream(shaderPath);
			if (stream == null) {
				GlfwSetup.LOGGER.fatal("Could not load shader file: {}", shaderPath);
				throw new FileNotFoundException();
			}
			final String shaderSource = new String(stream.readAllBytes());
			stream.close();
			glShaderSource(shader, shaderSource);
			glCompileShader(shader);
			if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE) {
				throw new IOException();
			}
		} catch (final IOException e) {
			GlfwSetup.LOGGER.fatal("Could not compile shader: {}", shaderPath);
			throw new ExitException();
		}
	}
}
