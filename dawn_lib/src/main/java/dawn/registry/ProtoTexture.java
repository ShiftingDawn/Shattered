package dawn.registry;

public interface ProtoTexture extends RegistryObject {

	interface Default extends ProtoTexture {}

	interface Stitched extends ProtoTexture {

		int getSpriteCount();

		int getSpriteWidth();

		int getSpriteHeight();
	}

	interface Bordered extends ProtoTexture {

		int getBorderTop();

		int getBorderBottom();

		int getBorderLeft();

		int getBorderRight();
	}

	interface Animation extends ProtoTexture {

		double getFps();

		Integer[] getFrameMapping();
	}
}
