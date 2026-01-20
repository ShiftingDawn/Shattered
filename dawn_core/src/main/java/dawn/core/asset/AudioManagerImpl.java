package dawn.core.asset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import dawn.Identifier;
import dawn.asset.Audio;
import dawn.asset.AudioManager;
import dawn.registry.ProtoAudio;
import dawn.registry.Registries;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.libc.LibCStdlib;
import static dawn.registry.ProtoAudio.getSegmentKey;
import static org.lwjgl.openal.AL10.AL_BITS;
import static org.lwjgl.openal.AL10.AL_CHANNELS;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.AL_FREQUENCY;
import static org.lwjgl.openal.AL10.AL_SIZE;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGetBufferi;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

@RequiredArgsConstructor
final class AudioManagerImpl implements AudioManager {

	private static final Logger LOGGER = LogManager.getLogger("Audio");
	private final List<ProtoAudio> loadedAudio = new CopyOnWriteArrayList<>();
	private final ConcurrentHashMap<Identifier, Audio> audio = new ConcurrentHashMap<>();
	private final AssetManagerImpl assets;

	@Override
	public Audio getSegment(final Identifier identifier, final String segment) {
		return this.audio.get(getSegmentKey(identifier, segment));
	}

	@Override
	public Audio get(final Identifier identifier) {
		return this.audio.get(identifier);
	}

	void init() {
		AudioManagerImpl.LOGGER.info("Reloading audio");
		for (final ProtoAudio proto : this.loadedAudio) {
			this.unloadAudio(proto);
		}
		this.loadedAudio.clear();
		for (final ProtoAudio proto : Registries.get().audio()) {
			this.loadAudio(proto);
		}
	}

	private void loadAudio(final ProtoAudio proto) {
		AudioManagerImpl.LOGGER.debug("\tLoading audio {}", proto.getRegistryKey());
		if (this.loadedAudio.contains(proto)) {
			AudioManagerImpl.LOGGER.error("\tTrying to load duplicate audio {}, skipping.", proto.getRegistryKey());
			return;
		}
		try {
			for (final String segment : proto.getSegments()) {
				AudioManagerImpl.LOGGER.debug("\t\tProcessing segment '{}'", segment);
				final Identifier segmentKey = getSegmentKey(proto.getRegistryKey(), segment);
				final String path = this.assets.getResources().makePath(segmentKey, "audio", "ogg");
				final URL url = this.assets.getResources().getResource(path);
				if (url == null) {
					throw new FileNotFoundException("Audio file does not exist. Expected path: " + path);
				}
				final ByteBuffer buffer = this.assets.getResources().getBuffer(path);
				if (buffer == null) {
					throw new IOException("Could not load audio from disk");
				}
				try (MemoryStack stack = stackPush()) {
					AudioManagerImpl.LOGGER.debug("\t\tAllocating audio data");
					final IntBuffer channelsPtr = stack.mallocInt(1);
					final IntBuffer sampleRatePtr = stack.mallocInt(1);
					AudioManagerImpl.LOGGER.debug("\t\tDecoding data");
					final ShortBuffer audio = stb_vorbis_decode_memory(buffer, channelsPtr, sampleRatePtr);
					assert audio != null;
					final int channels = channelsPtr.get(0);
					final int sampleRate = sampleRatePtr.get(0);
					final int format = channelsPtr.get(0) == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;
					final int id = AudioManagerImpl.makeAudioId();
					AudioManagerImpl.LOGGER.debug("\t\tUploading data");
					alBufferData(id, format, audio, sampleRatePtr.get());
					this.audio.put(segmentKey, new AudioImpl(proto, id, segment, channels, sampleRate, format, AudioManagerImpl.calculateLength(id)));
					LibCStdlib.free(audio);
				}
			}
		} catch (final IOException e) {
			AudioManagerImpl.LOGGER.error("Could not load audio {}. Skipping.", proto.getRegistryKey());
			AudioManagerImpl.LOGGER.error(e);
		}
	}

	private void unloadAudio(final ProtoAudio proto) {
		for (final String segment : proto.getSegments()) {
			final Identifier segmentKey = getSegmentKey(proto.getRegistryKey(), segment);
			final Audio audio = this.audio.get(segmentKey);
			if (audio != null) {
				AudioManagerImpl.LOGGER.debug("\tUnloading audio {}[segment={}]({})", proto.getRegistryKey(), segment, audio.id());
				alDeleteBuffers(audio.id());
				this.audio.remove(segmentKey);
				AudioManagerImpl.LOGGER.debug("\t\tDone");
			}
		}
	}

	private static int makeAudioId() {
		return alGenBuffers();
	}

	private static int calculateLength(final int id) {
		final int size = alGetBufferi(id, AL_SIZE);
		final int channels = alGetBufferi(id, AL_CHANNELS);
		final int bits = alGetBufferi(id, AL_BITS);
		final int frequency = alGetBufferi(id, AL_FREQUENCY);
		final float sampleLength = size * 8F / ((float) channels * (float) bits);
		return (int) Math.ceil((sampleLength / frequency) * 1000F);
	}
}
