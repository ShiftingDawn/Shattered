package dawn.asset;

import java.nio.ByteBuffer;
import lombok.Getter;
import org.jspecify.annotations.NullUnmarked;
import org.lwjgl.stb.STBIWriteCallback;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.memFree;

@NullUnmarked
@Getter
final class InMemoryPngWriter extends STBIWriteCallback {

	private ByteBuffer data;
	private int size;

	@Override
	public void invoke(final long context, final long data, final int size) {
		this.data = memAlloc(size);
		memCopy(STBIWriteCallback.getData(data, size), this.data);
		this.size = size;
	}

	@Override
	public void free() {
		memFree(this.data);
		super.free();
	}
}