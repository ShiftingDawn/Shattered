package dawn.lib.option;

import dawn.event.Event;

public interface OptionChangedEvent extends Event {

	String key();

	OptionSupplier options();
}
