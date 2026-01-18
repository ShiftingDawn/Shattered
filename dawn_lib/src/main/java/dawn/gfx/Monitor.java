package dawn.gfx;

import org.jspecify.annotations.Nullable;

public interface Monitor {

	String name();

	long pointer();

	boolean primary();

	@Nullable
	MonitorVideoMode currentMode();

	MonitorVideoMode[] allModes();
}
