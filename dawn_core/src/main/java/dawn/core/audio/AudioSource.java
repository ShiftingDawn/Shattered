package dawn.core.audio;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import dawn.Identifier;
import dawn.asset.AssetManager;
import dawn.asset.Audio;
import dawn.registry.ProtoAudio;
import dawn.registry.Registries;
import lombok.Getter;
import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alSourcei;

public final class AudioSource {

	private final @Getter AudioChannel channel;
	private final @Getter int id;
	private final Timer timer;
	private final AtomicInteger segment = new AtomicInteger(0);

	private AudioSource(final AudioChannel channel, final int id) {
		this.channel = channel;
		this.id = id;
		this.timer = this.newTimer();
	}

	public void play(final ProtoAudio audio) {
		this.segment.set(0);
		this.bind(audio);
		SoundSystem.play(this);
		this.scheduleNextSegment(audio);
	}

	public void play(Identifier audio) {
		ProtoAudio proto = Registries.get().audio().get(audio);
		if (proto != null) {
			this.play(proto);
		}
	}

	private void scheduleNextSegment(final ProtoAudio proto) {
		final Audio audio = this.getAudio(proto, this.segment.get());
		this.timer.schedule(new TimerTask() {

			@Override
			public void run() {
				AudioSource.this.nextSegment(audio.proto());
			}
		}, audio.lengthMillis());
	}

	private void nextSegment(final ProtoAudio proto) {
		final int segment = this.segment.incrementAndGet();
		if (segment == proto.getSegmentOrder().length) {
			if (proto.loopLastSegment()) {
				this.segment.decrementAndGet();
				SoundSystem.play(this);
				this.scheduleNextSegment(proto);
			} else {
				this.channel.destroy(this);
			}
		} else {
			this.bind(proto);
			SoundSystem.play(this);
			this.scheduleNextSegment(proto);
		}
	}

	private void bind(final ProtoAudio audio) {
		final Audio realAudio = this.getAudio(audio, this.segment.get());
		alSourcei(this.id, AL_BUFFER, realAudio.id());
	}

	private Audio getAudio(final ProtoAudio proto, final int segment) {
		final AssetManager assets = this.channel.getSoundSystem().getAssets();
		return assets.audio().getSegment(proto, proto.getSegmentOrder()[segment]);
	}

	public void destroy() {
		this.timer.cancel();
		SoundSystem.stop(this);
		alDeleteSources(this.id);
	}

	private Timer newTimer() {
		return new Timer("SoundSystem-%s".formatted(this.id), true);
	}

	static AudioSource make(final AudioChannel channel) {
		final int id = alGenSources();
		return new AudioSource(channel, id);
	}
}
