package shattered.lib;

import java.nio.file.Path;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import shattered.lib.event.EventBus;
import shattered.lib.gfx.Display;
import shattered.lib.util.Input;

@ApiStatus.Internal
public class Internal {

	public static String NAME;

	public static Path ROOT_PATH;

	public static Function<String, EventBus> EVENT_BUS_GENERATOR;

	public static Display DISPLAY;
	public static Input INPUT;
}
