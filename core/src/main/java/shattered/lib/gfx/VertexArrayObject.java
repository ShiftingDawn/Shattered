package shattered.lib.gfx;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class VertexArrayObject {

	private final int id;

	public VertexArrayObject() {
		this.id = glGenVertexArrays();
		this.bind();
	}

	public void bind() {
		glBindVertexArray(this.id);
	}
}
