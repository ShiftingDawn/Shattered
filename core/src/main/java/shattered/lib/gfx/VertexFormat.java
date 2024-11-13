package shattered.lib.gfx;

import java.util.ArrayList;

public final class VertexFormat {

	final VertexFormatElement[] elements;
	final int[] offsets;
	final int size;

	VertexFormat(final VertexFormatElement[] elements) {
		this.elements = elements;
		this.offsets = new int[elements.length];
		int totalSize = 0;
		for (int i = 0; i < this.elements.length; ++i) {
			this.offsets[i] = totalSize;
			totalSize += (this.elements[i].type.byteSize * this.elements[i].elements);
		}
		this.size = totalSize;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private final ArrayList<VertexFormatElement> elements = new ArrayList<>();

		private Builder() {
		}

		public Builder add(final VertexFormatElement element) {
			this.elements.add(element);
			return this;
		}

		public VertexFormat build() {
			return new VertexFormat(this.elements.toArray(new VertexFormatElement[0]));
		}
	}
}
