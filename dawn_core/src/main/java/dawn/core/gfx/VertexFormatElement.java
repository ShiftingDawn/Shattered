package dawn.core.gfx;

import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;

record VertexFormatElement(Type type, int index, int elements) {

	public enum Type {
		FLOAT(Float.BYTES, "Float", GL_FLOAT),
		BYTE_UNSIGNED(Byte.BYTES, "Unsigned Byte", GL_UNSIGNED_BYTE),
		BYTE(Byte.BYTES, "Byte", GL_BYTE),
		INT_UNSIGNED(Integer.BYTES, "Unsigned Integer", GL_UNSIGNED_INT),
		INT(Integer.BYTES, "Integer", GL_INT);

		final int byteSize;
		final String name;
		final int glConstant;

		Type(final int byteSize, final String name, final int glConstant) {
			this.byteSize = byteSize;
			this.name = name;
			this.glConstant = glConstant;
		}
	}
}
