package dawn.registry;

import dawn.Identifier;

public interface ProtoAudio extends RegistryObject {

	String SELF_SEGMENT = "";

	String[] getSegments();

	String[] getSegmentOrder();

	boolean loopLastSegment();

	static Identifier getSegmentKey(final Identifier identifier, final String segment) {
		return ProtoAudio.SELF_SEGMENT.equals(segment) ? identifier : identifier.withSuffix("." + segment);
	}
}
