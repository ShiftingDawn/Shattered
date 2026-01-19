package dawn.core.gfx;

import java.nio.ByteBuffer;
import dawn.gfx.Color;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

final class BufferBuilder {

	private final VertexFormat format;
	private final int drawMode;
	private final VertexArrayObject vao;
	private final Runnable callback;
	private final ByteBuffer buffer;
	private int vertices = 0;

	public BufferBuilder(final VertexFormat format, final int count, final int drawMode, final Runnable callback) {
		this.format = format;
		this.buffer = MemoryUtil.memAlloc(format.size * count);
		this.drawMode = drawMode;
		this.vao = VertexArrayObject.getInstance();
		this.callback = callback;
	}

	public BufferBuilder position(final float x, final float y) {
		this.buffer.putFloat(x);
		this.buffer.putFloat(y);
		this.buffer.putFloat(0f);
		return this;
	}

	public BufferBuilder color(final float r, final float g, final float b, final float a) {
		this.buffer.putFloat(r);
		this.buffer.putFloat(g);
		this.buffer.putFloat(b);
		this.buffer.putFloat(a);
		return this;
	}

	public BufferBuilder color(final Color color) {
		this.buffer.putFloat(color.r());
		this.buffer.putFloat(color.g());
		this.buffer.putFloat(color.b());
		this.buffer.putFloat(color.a());
		return this;
	}

	public BufferBuilder uv(final float u, final float v) {
		this.buffer.putFloat(u);
		this.buffer.putFloat(v);
		return this;
	}

	public void endVertex() {
		++this.vertices;
	}

	public void draw() {
		this.buffer.flip();
		final VertexBufferObject vbo = new VertexBufferObject();
		vbo.bind();
		vbo.uploadData(this.buffer, GL_STREAM_DRAW);
		this.callback.run();
		this.vao.bind();
		for (final VertexFormatElement element : this.format.elements) {
			glEnableVertexAttribArray(element.index());
		}
		final int stride = this.format.size;
		for (int i = 0; i < this.format.elements.length; ++i) {
			final VertexFormatElement element = this.format.elements[i];
			glVertexAttribPointer(element.index(), element.elements(), element.type().glConstant, false, stride, this.format.offsets[i]);
		}
		glDrawArrays(this.drawMode, 0, this.vertices);
		for (final VertexFormatElement element : this.format.elements) {
			glDisableVertexAttribArray(element.index());
		}
		vbo.unbind();
		vbo.destroy();
		MemoryUtil.memFree(this.buffer);
	}
}
