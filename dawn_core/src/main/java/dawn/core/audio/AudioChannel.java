package dawn.core.audio;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import static dawn.core.audio.SoundSystem.LOGGER;

public final class AudioChannel {

	private final Set<AudioSource> sources = Collections.newSetFromMap(new IdentityHashMap<>());
	private final @Getter SoundSystem soundSystem;
	private final AudioCategory category;
	private final @Getter int maxSources;

	public AudioChannel(final SoundSystem soundSystem, final AudioCategory category, final int maxSources) {
		this.soundSystem = soundSystem;
		this.category = category;
		this.maxSources = maxSources;
	}

	public @Nullable AudioSource getSource() {
		if (this.getActiveSources() >= this.getMaxSources()) {
			LOGGER.warn("AudioChannel reached source limit of {}", this.maxSources);
			return null;
		}
		final AudioSource source = AudioSource.make(this);
		this.sources.add(source);
		return source;
	}

	public void destroy(final AudioSource source) {
		if (this.sources.remove(source)) {
			source.destroy();
		}
	}

	public void destroyAll() {
		this.sources.forEach(AudioSource::destroy);
		this.sources.clear();
	}

	public int getActiveSources() {
		return this.sources.size();
	}
}
