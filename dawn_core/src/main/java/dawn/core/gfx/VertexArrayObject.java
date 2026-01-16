package dawn.core.gfx;

import org.jspecify.annotations.Nullable;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

final class VertexArrayObject {

	private static @Nullable VertexArrayObject instance;
	private final int id;

	private VertexArrayObject() {
		this.id = glGenVertexArrays();
		this.bind();
	}

	public void bind() {
		glBindVertexArray(this.id);
	}

	public static VertexArrayObject getInstance() {
		if (VertexArrayObject.instance == null) {
			VertexArrayObject.instance = new VertexArrayObject();
		}
		return VertexArrayObject.instance;
	}
}
