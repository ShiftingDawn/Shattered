package shattered.core;

import shattered.bridge.ShatteredEntryPoint;
import shattered.core.event.EventBusImpl;

@ShatteredEntryPoint
public final class Shattered {

	private Shattered(final String[] args) {
		System.out.println(args[0]);
		EventBusImpl.init();
	}
}
