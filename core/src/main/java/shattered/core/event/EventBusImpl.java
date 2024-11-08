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
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shattered.bridge.RuntimeMetadata;
import shattered.lib.FileHelper;
import shattered.lib.Internal;
import shattered.lib.event.Event;
import shattered.lib.event.EventBus;
import shattered.lib.event.EventBusSubscriber;
import shattered.lib.event.Subscribe;

public class EventBusImpl implements EventBus {

	public static final EventBusImpl INSTANCE = new EventBusImpl();
	static final Logger LOGGER = LogManager.getLogger("EventBus");
	static final boolean DUMP_CLASSES = Boolean.getBoolean("shattered.eventbus.dumpclasses");
	static final File DUMP_CLASSES_DIR = EventBusImpl.DUMP_CLASSES ? new File("debug/eventbus/classdump") : null;
	private final Object2ObjectMap<Object, List<EventHandler>> handlerMapping = new Object2ObjectArrayMap<>();
	private final ObjectSortedSet<EventHandler> sortedHandlers = new ObjectRBTreeSet<>((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));

	public static void init() {
		if (EventBusImpl.DUMP_CLASSES) {
			FileHelper.deleteRecursive(EventBusImpl.DUMP_CLASSES_DIR);
			EventBusImpl.DUMP_CLASSES_DIR.mkdirs();
		}
		Internal.DEFAULT_EVENT_BUS = EventBusImpl.INSTANCE;
		final String[] classNamesToSubscribe = RuntimeMetadata.getAnnotatedClasses(EventBusSubscriber.class);
		for (final String className : classNamesToSubscribe) {
			try {
				final Class<?> clazz = Class.forName(className);
				EventBusImpl.INSTANCE.register(clazz);
				EventBusImpl.LOGGER.debug("Registered @{} annotated class {}", EventBusSubscriber.class.getSimpleName(), className);
			} catch (final ClassNotFoundException e) {
				EventBusImpl.LOGGER.atError().withThrowable(e).log("Could not register event listener class {}", className);
			}
		}
	}

	@Override
	public void register(final Object object) {
		Objects.requireNonNull(object);
		if (this.handlerMapping.containsKey(object)) {
			EventBusImpl.LOGGER.atError().withThrowable(new Exception())
					.log("Cannot register event listeners for already registered class {}", object instanceof final Class<?> cls ? cls.getName() : object.getClass().getName());
			return;
		}
		final Method[] methods = EventBusImpl.findListenerMethods(object);
		final List<EventHandler> eventHandlers = new ArrayList<>();
		for (final Method method : methods) {
			eventHandlers.add(new EventHandler(method, object instanceof Class ? null : object));
		}
		this.handlerMapping.put(object, Collections.unmodifiableList(eventHandlers));
		this.sortedHandlers.addAll(eventHandlers);
	}

	@Override
	public void unregister(final Object object) {
		Objects.requireNonNull(object);
		if (!this.handlerMapping.containsKey(object)) {
			EventBusImpl.LOGGER.atError().withThrowable(new Exception())
					.log("\"Cannot unregister event listeners for unregistered class {}", object instanceof final Class<?> cls ? cls.getName() : object.getClass().getName());
			return;
		}
		this.sortedHandlers.removeAll(this.handlerMapping.get(object));
		this.handlerMapping.remove(object);
	}

	@Override
	public void post(final Event event) {
		for (final EventHandler handler : this.sortedHandlers) {
			if (handler.canPost(event)) {
				handler.postEvent(event);
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
