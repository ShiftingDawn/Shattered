package shattered.core.lib;

import shattered.core.Shattered;

public interface ShutdownHookCaller {

	void __executeShutdownHook();

	default void __registerShutdownHook(final boolean register) {
		if (register) {
			Shattered.getShattered().addShutdownHook(this);
		} else {
			Shattered.getShattered().removeShutdownHook(this);
		}
	}
}
