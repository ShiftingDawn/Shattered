package shattered.lib.event;

import lombok.Getter;

public abstract class Event {

    @Getter
    protected boolean cancelled = false;
}
