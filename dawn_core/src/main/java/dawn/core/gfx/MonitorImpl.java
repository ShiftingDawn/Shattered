package dawn.core.gfx;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import dawn.gfx.Monitor;
import dawn.gfx.MonitorVideoMode;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorName;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetVideoModes;

record MonitorImpl(String name, long pointer) implements Monitor {

	@Override
	public boolean primary() {
		return this.pointer() == glfwGetPrimaryMonitor();
	}

	@Override
	public @Nullable MonitorVideoMode currentMode() {
		final GLFWVidMode currentMode = glfwGetVideoMode(this.pointer);
		return currentMode != null ? MonitorVideoModeImpl.of(currentMode) : null;
	}

	@Override
	public MonitorVideoMode[] allModes() {
		final List<MonitorVideoMode> result = new ArrayList<>();
		final GLFWVidMode.Buffer buffer = glfwGetVideoModes(this.pointer);
		if (buffer != null) {
			final int amount = buffer.limit();
			for (int i = 0; i < amount; ++i) {
				result.add(MonitorVideoModeImpl.of(buffer.get(i)));
			}
		}
		return result.toArray(MonitorVideoMode[]::new);
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static Monitor of(final long monitor) {
		final String name = glfwGetMonitorName(monitor);
		return new MonitorImpl(Objects.requireNonNullElse(name, "Unknown"), monitor);
	}

	public static Monitor getPrimary() {
		return MonitorImpl.of(glfwGetPrimaryMonitor());
	}

	public static Monitor[] list() {
		final List<Monitor> result = new ArrayList<>();
		final PointerBuffer buffer = glfwGetMonitors();
		if (buffer != null) {
			final int amount = buffer.limit();
			for (int i = 0; i < amount; ++i) {
				result.add(MonitorImpl.of(buffer.get(i)));
			}
		}
		return result.toArray(Monitor[]::new);
	}
}
