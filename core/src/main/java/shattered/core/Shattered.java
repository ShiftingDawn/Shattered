package shattered.core;

import shattered.lib.ShatteredEntryPoint;

@ShatteredEntryPoint
public final class Shattered {

	private Shattered(final String[] args) {
		System.out.println(args.toString());
	}
}
