package shattered.core.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicLong;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import shattered.lib.event.Event;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V21;

public final class EventHandler implements EventDispatcher {

	private static final AtomicLong LISTENER_ID = new AtomicLong(0);
	private static final EventBusClassLoader LOADER = new EventBusClassLoader();
	private static final String GENERATED_CLASS_SUPER_NAME = Type.getInternalName(Object.class);
	private static final String[] GENERATED_CLASS_INTERFACES = new String[] { Type.getInternalName(EventDispatcher.class) };
	private static final Method HANDLE_METHOD = EventDispatcher.class.getDeclaredMethods()[0];

	private final EventDispatcher dispatcher;

	public EventHandler(final Method eventListenerMethod, final Object classInstance) {
		final boolean isStatic = Modifier.isStatic(eventListenerMethod.getModifiers());
		final String generatedClassName = eventListenerMethod.getDeclaringClass().getName().replace('.', '_') + "_" + eventListenerMethod.getName() + '_' + EventHandler.LISTENER_ID.getAndIncrement();

		final ClassWriter classWriter = new ClassWriter(0);
		classWriter.visit(V21, ACC_PUBLIC | ACC_FINAL | ACC_SUPER, generatedClassName, null, EventHandler.GENERATED_CLASS_SUPER_NAME, EventHandler.GENERATED_CLASS_INTERFACES);

		if (!isStatic) {
			final FieldVisitor fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL, "instance", Type.getDescriptor(classInstance.getClass()), null, null);
			fieldVisitor.visitEnd();
		}

		{
			final String constructorDescriptor = isStatic ? "()V" : Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(classInstance.getClass()));
			final MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", constructorDescriptor, null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, EventHandler.GENERATED_CLASS_SUPER_NAME, "<init>", "()V", false);
			if (isStatic) {
				methodVisitor.visitInsn(RETURN);
				methodVisitor.visitMaxs(1, 1);
			} else {
				methodVisitor.visitVarInsn(ALOAD, 0);
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitFieldInsn(PUTFIELD, generatedClassName, "instance", Type.getDescriptor(classInstance.getClass()));
				methodVisitor.visitInsn(RETURN);
				methodVisitor.visitMaxs(2, 2);
			}
			methodVisitor.visitEnd();
		}

		{
			final MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, EventHandler.HANDLE_METHOD.getName(), Type.getMethodDescriptor(EventHandler.HANDLE_METHOD), null, null);
			methodVisitor.visitCode();
			if (!isStatic) {
				methodVisitor.visitVarInsn(ALOAD, 0);
				methodVisitor.visitFieldInsn(GETFIELD, generatedClassName, "instance", Type.getDescriptor(classInstance.getClass()));
			}
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitMethodInsn(isStatic ? INVOKESTATIC : INVOKEVIRTUAL, Type.getInternalName(eventListenerMethod.getDeclaringClass()), eventListenerMethod.getName(),
					Type.getMethodDescriptor(eventListenerMethod), false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(isStatic ? 1 : 2, 2);
			methodVisitor.visitEnd();
		}

		classWriter.visitEnd();
		final Class<?> result = EventHandler.LOADER.create(generatedClassName, classWriter.toByteArray());
		try {
			this.dispatcher = isStatic ? (EventDispatcher) result.getDeclaredConstructor().newInstance() : (EventDispatcher) result.getDeclaredConstructors()[0].newInstance(classInstance);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		System.out.println();
	}

	@Override
	public void postEvent(final Event event) {
		this.dispatcher.postEvent(event);
	}

	private static class EventBusClassLoader extends ClassLoader {

		public EventBusClassLoader() {
			super(null);
		}

		@Override
		protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
			return Class.forName(name, resolve, EventBusClassLoader.class.getClassLoader());
		}

		public Class<?> create(final String name, final byte[] data) {
			return this.defineClass(name, data, 0, data.length);
		}
	}
}
