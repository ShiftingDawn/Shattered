module dawn.core {
	uses dawn.core.app.IBootApp;

	exports dawn.core.app to dawn.app;

	requires dawn.lib;
	requires org.apache.logging.log4j;
	requires org.apache.logging.log4j.core;
	requires static org.jspecify;
	requires static lombok;
	requires org.lwjgl.natives;
	requires org.lwjgl.glfw;
	requires org.lwjgl.glfw.natives;
	requires org.lwjgl.opengl;
	requires org.lwjgl.opengl.natives;
	requires org.lwjgl.stb;
	requires org.lwjgl.stb.natives;
	requires it.unimi.dsi.fastutil;
	requires com.google.gson;
	requires org.joml;

	opens dawn.core to com.google.gson;
	opens dawn.core.registry to com.google.gson;
}