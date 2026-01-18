package dawn.core.app;

import dawn.Dawn;
import dawn.core.DawnImpl;
import dawn.lib.RunOnce;
import dawn.lib.option.OptionSupplier;
import lombok.Getter;

public abstract class IBootApp {

	private final RunOnce initialized = new RunOnce();
	private @Getter Dawn dawn;

	public final void preInit(final Dawn dawn) {
		this.initialized.test(() -> "BootApp has already been initialized");
		this.dawn = dawn;
		this.preInit();
	}

	protected abstract void preInit();

	public abstract void init();

	public abstract OptionSupplier getOptions();

	public final void stop() {
		if (!(this.dawn instanceof final DawnImpl impl)) {
			throw new IllegalStateException("BootApp has not been initialized correctly");
		}
		impl.stop();
	}
}
