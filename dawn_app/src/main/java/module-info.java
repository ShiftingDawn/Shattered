module dawn.app {
	requires dawn.core;
	requires dawn.lib;
	requires static org.jspecify;
	requires static lombok;
	requires org.apache.logging.log4j;

	provides dawn.core.app.IBootApp with dawn.app.DawnApp;
}