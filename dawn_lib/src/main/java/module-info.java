module dawn.lib {
	requires static org.jspecify;
	requires org.apache.logging.log4j;
	requires static lombok;
	requires com.google.gson;
	requires org.joml;

	exports dawn;
	exports dawn.internal to dawn.core;
	exports dawn.init;
	exports dawn.lib;
	exports dawn.registry;
	exports dawn.event;
	exports dawn.gfx;
	exports dawn.input;
	exports dawn.asset;
	exports dawn.gui;
	exports dawn.dawndb;
	exports dawn.lib.option;
}