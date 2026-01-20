package dawn.core.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import dawn.Identifier;
import dawn.lib.ResourceFinder;
import dawn.registry.ProtoAudio;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullUnmarked;

final class ProtoAudioContentFactory extends BaseRegistryContentFactory<ProtoAudio> {

	@Override
	public ProtoAudio make(final Logger logger, final ResourceFinder resources, final Identifier registryKey) {
		this.printDescription(logger, "audio", registryKey);
		final JsonData data = this.loadJsonData(JsonData.class, logger, resources, registryKey, "audio", true)
			.orElseGet(JsonData::new);

		final String[] segments = data.segments != null && data.segments.length > 0 ? data.segments : new String[] { ProtoAudio.SELF_SEGMENT };
		final ArrayList<String> segmentOrderList = new ArrayList<>(List.of(data.segmentOrder != null && data.segmentOrder.length > 0 ? data.segmentOrder : Arrays.copyOf(segments, segments.length)));
		final Iterator<String> iterator = segmentOrderList.iterator();
		while (iterator.hasNext()) {
			final String segmentOrderItem = iterator.next();
			if (!Arrays.asList(segments).contains(segmentOrderItem)) {
				logger.error("Segment order contains unknown segment '{}'. Segment will be removed");
				iterator.remove();
			}
		}
		return new ProtoAudioImpl(registryKey, segments, segmentOrderList.toArray(String[]::new), data.loopLastSegment != null && data.loopLastSegment);
	}

	@NullUnmarked
	private static class JsonData {

		@SerializedName("segments")
		public String[] segments;

		@SerializedName("segment_order")
		public String[] segmentOrder;

		@SerializedName("loop_last_segment")
		public Boolean loopLastSegment;
	}
}
