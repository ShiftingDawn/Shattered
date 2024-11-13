package shattered.lib.gfx;

public final class GeneralVertexFormats {

	//Elements
	public static final VertexFormatElement ELEMENT_POSITION = new VertexFormatElement(VertexFormatElement.Type.FLOAT, 0, 3);
	public static final VertexFormatElement ELEMENT_COLOR = new VertexFormatElement(VertexFormatElement.Type.FLOAT, 1, 4);
	public static final VertexFormatElement ELEMENT_TEXTURE_UV = new VertexFormatElement(VertexFormatElement.Type.FLOAT, 2, 2);
	public static final VertexFormatElement ELEMENT_TEXTURE_UV_START = new VertexFormatElement(VertexFormatElement.Type.FLOAT, 3, 2);
	public static final VertexFormatElement ELEMENT_TEXTURE_UV_SIZE = new VertexFormatElement(VertexFormatElement.Type.FLOAT, 4, 2);

	//Formats
	public static final VertexFormat FORMAT_TEXTURE = VertexFormat.builder()
			.add(GeneralVertexFormats.ELEMENT_POSITION)
			.add(GeneralVertexFormats.ELEMENT_COLOR)
			.add(GeneralVertexFormats.ELEMENT_TEXTURE_UV)
			.add(GeneralVertexFormats.ELEMENT_TEXTURE_UV_START)
			.add(GeneralVertexFormats.ELEMENT_TEXTURE_UV_SIZE)
			.build();

	public static final VertexFormat FORMAT_COLOR = VertexFormat.builder()
			.add(GeneralVertexFormats.ELEMENT_POSITION)
			.add(GeneralVertexFormats.ELEMENT_COLOR)
			.build();

	private GeneralVertexFormats() {
	}
}