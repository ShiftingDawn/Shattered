package shattered.core;

import java.util.concurrent.atomic.AtomicBoolean;
import shattered.core.gfx.Display;
import shattered.core.gfx.RenderContext;

public final class Runtime {

	private final AtomicBoolean RUNNING = new AtomicBoolean(false);
	private final RenderContext renderContext = new RenderContext();

	Runtime() {
	}

	void start() {
		this.RUNNING.set(true);
		this.renderContext.init(Display.getWindowId());
		while (this.RUNNING.get()) {
			this.renderContext.render();
		}
		this.renderContext.destroy();
	}

	void stop() {
		this.RUNNING.set(false);
	}
}
