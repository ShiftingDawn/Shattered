package dawn.core.registry;

import dawn.Identifier;
import dawn.registry.ProtoAudio;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
final class ProtoAudioImpl implements ProtoAudio {

	private final Identifier registryKey;
	private final String[] segments;
	private final String[] segmentOrder;
	private final boolean loopLastSegment;

	@Override
	public boolean loopLastSegment() {
		return this.loopLastSegment;
	}
}
