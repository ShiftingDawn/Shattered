package shattered.core;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shattered.bridge.ShatteredEntryPoint;
import shattered.core.event.EventBusImpl;
import shattered.core.gfx.Display;
import shattered.core.lib.ShutdownHookCaller;
import shattered.lib.Internal;

@ShatteredEntryPoint
public final class Shattered {

	public static final String NAME = "Shattered";
	public static final Logger LOGGER = LogManager.getLogger(Shattered.NAME);
	@Getter
	private static Shattered shattered;
	@Getter
	private final Runtime runtime = new Runtime();
	private final Set<ShutdownHookCaller> shutdownHooks = Collections.newSetFromMap(new WeakHashMap<>());

	private Shattered(final String[] args) {
		Shattered.shattered = this;
		Internal.ROOT_PATH = Paths.get(args[0]).toAbsolutePath();
		//TODO handle args
		this.init();
		this.start();
	}

	private void init() {
		EventBusImpl.init();
		Display.init();
	}

	private void start() {
		try {
			this.runtime.start();
		} catch (final ExitShatteredException e) {
			if (e.getMessage() != null) {
				Shattered.LOGGER.fatal(e);
			}
			try {
				this.stop();
			} catch (final Throwable ignored) {
				//Don't care, we are already in an error-state
			}
		}
	}

	public void addShutdownHook(final ShutdownHookCaller hook) {
		this.shutdownHooks.add(hook);
	}

	public void removeShutdownHook(final ShutdownHookCaller hook) {
		this.shutdownHooks.remove(hook);
	}

	public void stop() {
		Shattered.LOGGER.warn("{} will now exit", Shattered.NAME);
		this.shutdownHooks.forEach(ShutdownHookCaller::__executeShutdownHook);
		this.runtime.stop();
	}
}
