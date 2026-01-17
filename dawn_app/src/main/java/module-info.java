module dawn.app {
	requires dawn.core;
	requires static org.jspecify;
	requires dawn.lib;
	requires static lombok;

	provides dawn.core.app.IBootApp with dawn.app.DawnApp;
}