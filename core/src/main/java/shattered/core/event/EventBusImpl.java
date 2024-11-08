package shattered.core.event;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import shattered.lib.FileHelper;
import shattered.lib.Internal;
import shattered.lib.event.Event;
import shattered.lib.event.EventBus;
import shattered.lib.event.Subscribe;

public class EventBusImpl implements EventBus {

	public static final EventBusImpl INSTANCE = new EventBusImpl();
	static final boolean DUMP_CLASSES = Boolean.getBoolean("shattered.eventbus.dumpclasses");
	static final File DUMP_CLASSES_DIR = EventBusImpl.DUMP_CLASSES ? new File("debug/eventbus/classdump") : null;
	private final Object2ObjectMap<Object, List<EventHandler>> handlers = new Object2ObjectArrayMap<>();

	public static void init() {
		if (EventBusImpl.DUMP_CLASSES) {
			FileHelper.deleteRecursive(EventBusImpl.DUMP_CLASSES_DIR);
			EventBusImpl.DUMP_CLASSES_DIR.mkdirs();
		}
		Internal.DEFAULT_EVENT_BUS = EventBusImpl.INSTANCE;
	}

	@Override
	public void register(final Object object) {
		Objects.requireNonNull(object);
		if (this.handlers.containsKey(object)) {
			//TODO better logging
			System.err.printf("Cannot register event listeners for already registered class %s%n", object instanceof final Class<?> cls ? cls.getName() : object.getClass().getName());
			new Exception().printStackTrace();
			return;
		}
		final Method[] methods = EventBusImpl.findListenerMethods(object);
		final List<EventHandler> eventHandlers = new ArrayList<>();
		for (final Method method : methods) {
			eventHandlers.add(new EventHandler(method, object instanceof Class ? null : object));
		}
		this.handlers.put(object, Collections.unmodifiableList(eventHandlers));
	}

	@Override
	public void post(final Event event) {
		for (final List<EventHandler> handlers : this.handlers.values()) {
			for (final EventHandler handler : handlers) {
				if (handler.canPost(event)) {
					handler.postEvent(event);
				}
			}
		}
	}

	private static Method[] findListenerMethods(final Object object) {
		final Class<?> clazz = object instanceof final Class<?> cls ? cls : object.getClass();
		final List<Method> result = new ArrayList<>();
		for (final Method method : clazz.getDeclaredMethods()) {
			if (!method.isAnnotationPresent(Subscribe.class)) {
				continue;
			}
			if (clazz != object && Modifier.isStatic(method.getModifiers())) {
				throw new IllegalStateException("Static event subscriber %s cannot be registered, only non-static methods are allowed when registering instanced objects".formatted(method.toString()));
			}
			if (clazz == object && !Modifier.isStatic(method.getModifiers())) {
				throw new IllegalStateException("Non-static event subscriber %s cannot be registered, only static methods are allowed when registering classes".formatted(method.toString()));
			}
			if (method.getReturnType() != Void.TYPE) {
				throw new IllegalStateException("Event subscriber %s must return void".formatted(method.toString()));
			}
			if (method.getParameterCount() != 1) {
				throw new IllegalStateException("Event subscriber %s must have exactly 1 parameter".formatted(method.toString()));
			}
			if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
				throw new IllegalStateException("Parameter of event subscriber %s must extend %s".formatted(method.toString(), Event.class.getName()));
			}
			result.add(method);
		}
		return result.toArray(Method[]::new);
	}
}
