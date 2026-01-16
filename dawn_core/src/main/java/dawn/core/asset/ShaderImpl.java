package dawn.core.asset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import dawn.asset.Shader;
import dawn.core.DawnImpl;
import dawn.core.ExitException;
import dawn.lib.ResourceFinder;
import dawn.registry.ProtoShader;
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
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

final class ShaderImpl implements Shader {

	private final ProtoShader proto;
	private final int program;

	ShaderImpl(final ResourceFinder resources, final ProtoShader proto) {
		this.proto = proto;
		//Generate shaders and program
		final int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		final int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		this.program = glCreateProgram();
		//Load and compile shaders
		ShaderImpl.compileShader(vertexShader, resources, resources.makePath(proto.getRegistryKey(), "shader", "vert"));
		ShaderImpl.compileShader(fragmentShader, resources, resources.makePath(proto.getRegistryKey(), "shader", "frag"));
		//Configure shaders and program
		glAttachShader(this.program, fragmentShader);
		glAttachShader(this.program, vertexShader);
		glBindFragDataLocation(fragmentShader, 0, proto.getPropOutColor());
		glLinkProgram(this.program);
		if (glGetProgrami(this.program, GL_LINK_STATUS) != GL_TRUE) {
			DawnImpl.LOGGER.fatal("Could not link shader program");
			throw new ExitException();
		}
		//Delete shaders
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	private static void compileShader(final int shader, final ResourceFinder resources, final String shaderPath) {
		try {
			final InputStream stream = resources.getStream(shaderPath);
			if (stream == null) {
				DawnImpl.LOGGER.fatal("Could not load shader file: {}", shaderPath);
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
			DawnImpl.LOGGER.fatal("Could not compile shader: {}", shaderPath);
			throw new ExitException();
		}
	}

	@Override
	public ProtoShader proto() {
		return this.proto;
	}

	@Override
	public int getProgram() {
		return this.program;
	}
}
