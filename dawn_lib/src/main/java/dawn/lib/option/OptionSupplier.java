package dawn.lib.option;

public interface OptionSupplier {

	ManagedBoolean isFullscreen();

	ManagedBoolean enableVerticalSync();

	ManagedInt getGuiScale();
}
