package dawn.core.audio;

import java.util.EnumMap;
import dawn.asset.AssetManager;
import dawn.asset.Audio;
import dawn.registry.ProtoAudio;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import static org.lwjgl.openal.AL10.AL_FALSE;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_TRUE;
import static org.lwjgl.openal.AL10.alGetSourcef;
import static org.lwjgl.openal.AL10.alSourcePause;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourcei;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;

public final class SoundSystem implements AutoCloseable {

	private static final int MAX_SOURCES = 32;
	static final Logger LOGGER = LogManager.getLogger("SoundSystem");
	private final EnumMap<AudioCategory, AudioChannel> channels = new EnumMap<>(AudioCategory.class);
	private final @Getter AssetManager assets;
	private final @Getter long device;
	private final @Getter long context;

	public SoundSystem(final AssetManager assets) {
		this.assets = assets;
		final String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		this.device = alcOpenDevice(defaultDeviceName);
		final int[] attributes = { 0 };
		this.context = alcCreateContext(this.device, attributes);
		alcMakeContextCurrent(this.context);
		final ALCCapabilities alcCaps = ALC.createCapabilities(this.device);
		AL.createCapabilities(alcCaps);
		for (final AudioCategory category : AudioCategory.values()) {
			this.channels.put(category, new AudioChannel(this, category, SoundSystem.MAX_SOURCES));
		}
	}

	public AudioChannel getChannel(final AudioCategory category) {
		return this.channels.get(category);
	}

	public void play(final ProtoAudio audio, final AudioCategory category) {
		final AudioSource source = this.getChannel(category).getSource();
		if (source != null) {
			source.play(audio);
		}
	}

	@Override
	public void close() {
		alcDestroyContext(this.context);
		alcCloseDevice(this.device);
	}

	public static void play(final AudioSource source) {
		alSourcePlay(source.getId());
	}

	public static void pause(final AudioSource source) {
		alSourcePause(source.getId());
	}

	public static void stop(final AudioSource source) {
		SoundSystem.noLoop(source);
		alSourceStop(source.getId());
	}

	public static void loop(final AudioSource source) {
		alSourcei(source.getId(), AL_LOOPING, AL_TRUE);
	}

	public static void noLoop(final AudioSource source) {
		alSourcei(source.getId(), AL_LOOPING, AL_FALSE);
	}

	public static long getMillisPlayed(final AudioSource source) {
		return Math.round(alGetSourcef(source.getId(), AL11.AL_SEC_OFFSET) * 1000);
	}

	public static long getMillisLeft(final Audio audio, final AudioSource source) {
		return audio.lengthMillis() - SoundSystem.getMillisPlayed(source);
	}
}
