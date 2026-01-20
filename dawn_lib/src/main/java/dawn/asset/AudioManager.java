package dawn.asset;

import dawn.Identifier;
import dawn.registry.ProtoAudio;

public interface AudioManager extends ProtoAssetProvider<ProtoAudio, Audio> {

	Audio getSegment(Identifier identifier, String segment);

	default Audio getSegment(final ProtoAudio proto, final String segment) {
		return this.getSegment(proto.getRegistryKey(), segment);
	}
}
