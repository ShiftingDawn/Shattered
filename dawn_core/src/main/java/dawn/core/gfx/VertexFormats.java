package dawn.core.gfx;

final class VertexFormats {

	//Elements
	public static final VertexFormatElement ELEMENT_POSITION = new VertexFormatElement(VertexFormatElement.Type.FLOAT, 0, 3);
	public static final VertexFormatElement ELEMENT_COLOR = new VertexFormatElement(VertexFormatElement.Type.FLOAT, 1, 4);
	public static final VertexFormatElement ELEMENT_TEXTURE_UV = new VertexFormatElement(VertexFormatElement.Type.FLOAT, 2, 2);

	//Formats
	public static final VertexFormat FORMAT_TEXTURE = VertexFormat.builder()
		.add(VertexFormats.ELEMENT_POSITION)
		.add(VertexFormats.ELEMENT_COLOR)
		.add(VertexFormats.ELEMENT_TEXTURE_UV)
		.build();

	public static final VertexFormat FORMAT_COLOR = VertexFormat.builder()
		.add(VertexFormats.ELEMENT_POSITION)
		.add(VertexFormats.ELEMENT_COLOR)
		.build();

	private VertexFormats() {
	}
}
