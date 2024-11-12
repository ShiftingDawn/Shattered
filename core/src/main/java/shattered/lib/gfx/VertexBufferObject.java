package shattered.lib.gfx;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

public final class VertexBufferObject {

	private final int target;
	private final int id;

	public VertexBufferObject(final int target) {
		this.target = target;
		this.id = glGenBuffers();
		this.bind();
	}

	public VertexBufferObject() {
		this(GL_ARRAY_BUFFER);
	}

	public void bind() {
		glBindBuffer(this.target, this.id);
	}

	public void uploadData(final FloatBuffer buffer, final int usage) {
		glBufferData(this.target, buffer, usage);
	}

	public void uploadData(final long size, final int usage) {
		glBufferData(this.target, size, usage);
	}

	public void uploadData(final IntBuffer buffer, final int usage) {
		glBufferData(this.target, buffer, usage);
	}

	public void uploadData(final ByteBuffer buffer, final int usage) {
		glBufferData(this.target, buffer, usage);
	}

	public void uploadSubData(final long offset, final FloatBuffer data) {
		glBufferSubData(this.target, offset, data);
	}

	public void unbind() {
		glBindBuffer(this.target, 0);
	}

	public void destroy() {
		this.unbind();
		glDeleteBuffers(this.id);
	}
}
