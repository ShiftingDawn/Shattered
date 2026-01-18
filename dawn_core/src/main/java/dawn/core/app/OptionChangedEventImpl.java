package dawn.core.app;

import dawn.lib.option.OptionChangedEvent;
import dawn.lib.option.OptionSupplier;

public record OptionChangedEventImpl(String key, OptionSupplier options) implements OptionChangedEvent {
}
