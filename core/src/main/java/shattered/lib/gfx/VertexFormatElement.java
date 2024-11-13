package shattered.lib.gfx;

import org.lwjgl.opengl.GL11;

public class VertexFormatElement {

	final Type type;
	final int index;
	final int elements;

	public VertexFormatElement(final Type type, final int index, final int elements) {
		this.type = type;
		this.index = index;
		this.elements = elements;
	}

	public enum Type {
		FLOAT(Float.BYTES, "Float", GL11.GL_FLOAT),
		BYTE_UNSIGNED(Byte.BYTES, "Unsigned Byte", GL11.GL_UNSIGNED_BYTE),
		BYTE(Byte.BYTES, "Byte", GL11.GL_BYTE),
		INT_UNSIGNED(Integer.BYTES, "Unsigned Integer", GL11.GL_UNSIGNED_INT),
		INT(Integer.BYTES, "Integer", GL11.GL_INT);

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
