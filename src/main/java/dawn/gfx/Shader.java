package dawn.gfx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import dawn.Dawn;
import dawn.asset.ResourceResolver;
import dawn.asset.ShaderAsset;
import dawn.lib.ExitException;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
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
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

public final class Shader {

	private final @Getter ShaderAsset asset;
	private final @Getter int program;

	private Shader(final ResourceResolver resources, final ShaderAsset asset, @Nullable final String vertexSource, @Nullable final String fragmentSource) {
		this.asset = asset;
		//Generate shaders and program
		final int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		final int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		this.program = glCreateProgram();
		//Load and compile shaders
		Shader.compileShader(vertexShader, resources, resources.makePath(asset.getRegistryKey(), "shader", "vert"), vertexSource);
		Shader.compileShader(fragmentShader, resources, resources.makePath(asset.getRegistryKey(), "shader", "frag"), fragmentSource);
		//Configure shaders and program
		glAttachShader(this.program, fragmentShader);
		glAttachShader(this.program, vertexShader);
		glBindFragDataLocation(fragmentShader, 0, asset.getPropOutColor());
		glLinkProgram(this.program);
		if (glGetProgrami(this.program, GL_LINK_STATUS) != GL_TRUE) {
			Dawn.LOGGER.fatal("Could not link shader program");
			throw new ExitException();
		}
		//Delete shaders
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	public Shader(final ResourceResolver resources, final ShaderAsset asset) {
		this(resources, asset, null, null);
	}

	public void bind() {
		GlStateManager.bindShader(this.program);
	}

	public void unbind() {
		GlStateManager.bindShader(0);
	}

	public void destroy() {
		glDeleteProgram(this.program);
	}

	private static void compileShader(final int shader, final ResourceResolver resources, final String shaderPath, @Nullable String shaderSource) {
		try {
			if (shaderSource == null) {
				final InputStream stream = resources.getStream(shaderPath);
				if (stream == null) {
					Dawn.LOGGER.fatal("Could not load shader file: {}", shaderPath);
					throw new FileNotFoundException();
				}
				shaderSource = new String(stream.readAllBytes());
				stream.close();
			}
			glShaderSource(shader, shaderSource);
			glCompileShader(shader);
			if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE) {
				throw new IOException();
			}
		} catch (final IOException e) {
			Dawn.LOGGER.fatal("Could not compile shader: {}", shaderPath);
			throw new ExitException();
		}
	}

	public static Shader newSimpleShader(final ResourceResolver resources, final ShaderAsset asset, final String vertexSource, final String fragmentSource) {
		return new Shader(resources, asset, vertexSource, fragmentSource);
	}
}
