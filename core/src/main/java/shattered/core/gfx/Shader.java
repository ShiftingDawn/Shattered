package shattered.core.gfx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import shattered.core.ExitShatteredException;
import shattered.core.Shattered;
import shattered.lib.util.Utils;
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
import static shattered.core.Shattered.LOGGER;

public final class Shader {

	private static final AtomicInteger ACTIVE = new AtomicInteger(-1);
	private static final String VERSION = "330 core";
	private final int vertShaderId;
	private final int fragShaderId;
	private final int programId;

	public Shader(final String vertShader, final String fragShader) {
		this.vertShaderId = glCreateShader(GL_VERTEX_SHADER);
		this.fragShaderId = glCreateShader(GL_FRAGMENT_SHADER);
		Shader.compileShader(this.vertShaderId, "/%s.vert".formatted(vertShader));
		Shader.compileShader(this.fragShaderId, "/%s.frag".formatted(fragShader));

		this.programId = glCreateProgram();
		glAttachShader(this.programId, this.vertShaderId);
		glAttachShader(this.programId, this.fragShaderId);
		glLinkProgram(this.programId);
		if (glGetProgrami(this.programId, GL_LINK_STATUS) != GL_TRUE) {
			throw new ExitShatteredException("Could not link GL Program");
		}
		this.setActive();
	}

	public void setActive() {
		if (Shader.ACTIVE.get() != this.programId) {
			Shader.ACTIVE.set(this.programId);
			glUseProgram(this.programId);
		}
	}

	private void destroy() {
		LOGGER.debug("Destroying shader {}", this.programId);
		glUseProgram(-1);
		glDeleteProgram(this.programId);
		LOGGER.debug("\tDestroying fragment shader {}", this.fragShaderId);
		glDeleteShader(this.fragShaderId);
		LOGGER.debug("\tDestroying vertex shader {}", this.vertShaderId);
		glDeleteShader(this.vertShaderId);
	}

	private static void compileShader(final int shaderId, final String path) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			final ByteBuffer header = stack.ASCII("#version %s\n#line 0\n".formatted(Shader.VERSION), false);
			final ByteBuffer code = Shader.getFileContents(path);

			glShaderSource(shaderId, stack.pointers(header, code), stack.ints(header.remaining(), code.remaining()));

			glCompileShader(shaderId);
			if (glGetShaderi(shaderId, GL_COMPILE_STATUS) != GL_TRUE) {
				throw new ExitShatteredException("Could not compile shader: " + path);
			}
		}
	}

	private static ByteBuffer getFileContents(final String path) {
		try (InputStream stream = Shattered.class.getResourceAsStream(path)) {
			if (stream == null) {
				throw new FileNotFoundException();
			}
			final byte[] data = stream.readAllBytes();
			return Utils.make(BufferUtils.createByteBuffer(data.length), buffer -> buffer.put(data));
		} catch (final IOException e) {
			LOGGER.error("Could not load shader data", e);
			throw new ExitShatteredException();
		}
	}
}
