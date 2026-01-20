package dawn.asset;

import dawn.registry.ProtoAudio;

public interface Audio extends ProtoAsset<ProtoAudio> {

	int id();

	String segment();

	int channels();

	int sampleRate();

	int audioFormat();

	int lengthMillis();
}
