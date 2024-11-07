package shattered.core;

import shattered.bridge.ShatteredEntryPoint;

@ShatteredEntryPoint
public final class Shattered {

	private Shattered(final String[] args) {
		System.out.println(args.toString());
	}
}
