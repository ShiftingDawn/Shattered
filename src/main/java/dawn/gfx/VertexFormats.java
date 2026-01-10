package dawn.gfx;

public final class VertexFormats {

	//Elements
	public static final VertexFormatElement ELEMENT_POSITION = new VertexFormatElement(VertexFormatElement.Type.FLOAT, 0, 3);
	public static final VertexFormatElement ELEMENT_COLOR = new VertexFormatElement(VertexFormatElement.Type.FLOAT, 1, 4);
	public static final VertexFormatElement ELEMENT_TEXTURE_UV = new VertexFormatElement(VertexFormatElement.Type.FLOAT, 2, 2);

	//Formats
	public static final VertexFormat FORMAT_TEXTURE = VertexFormat.builder()
		.add(ELEMENT_POSITION)
		.add(ELEMENT_COLOR)
		.add(ELEMENT_TEXTURE_UV)
		.build();

	public static final VertexFormat FORMAT_COLOR = VertexFormat.builder()
		.add(ELEMENT_POSITION)
		.add(ELEMENT_COLOR)
		.build();

	private VertexFormats() {
	}
}
