package dawn.core.asset;

import dawn.asset.Audio;
import dawn.registry.ProtoAudio;

record AudioImpl(ProtoAudio proto, int id, String segment, int channels, int sampleRate, int audioFormat, int lengthMillis) implements Audio {
}
