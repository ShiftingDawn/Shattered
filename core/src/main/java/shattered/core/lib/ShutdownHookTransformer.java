package shattered.core.lib;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import shattered.bridge.ClassTransformer;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM9;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;

public final class ShutdownHookTransformer implements ClassTransformer {

	private static final String DESC = Type.getDescriptor(ShutdownHook.class);
	private static final String IFACE = Type.getInternalName(ShutdownHookCaller.class);
	private static final Method IFACE_METHOD = Arrays.stream(ShutdownHookCaller.class.getDeclaredMethods()).filter(m -> m.getParameterCount() == 0).findFirst().orElseThrow();
	private static final String IFACE_METHOD_DESC = Type.getMethodDescriptor(ShutdownHookTransformer.IFACE_METHOD);
	private static final Method IFACE_METHOD_2 = Arrays.stream(ShutdownHookCaller.class.getDeclaredMethods()).filter(m -> m.getParameterCount() == 1).findFirst().orElseThrow();

	@Override
	public boolean canTransform(final ClassNode node) {
		return node.visibleAnnotations != null && node.visibleAnnotations.stream().anyMatch(annotationNode -> annotationNode.desc.equals(ShutdownHookTransformer.DESC));
	}

	@Override
	public byte[] transform(final byte[] data) {
		return this.handle(data, (node, asmVersion, parent) -> {
			final AnnotationNode annotationNode = node.visibleAnnotations.stream().filter(n -> n.desc.equals(ShutdownHookTransformer.DESC)).findFirst().orElseThrow();
			final String methodName = (String) annotationNode.values.get(annotationNode.values.indexOf("value") + 1);
			final MethodNode methodNode = node.methods.stream().filter(n -> n.name.equals(methodName)).findFirst()
					.orElseThrow(() -> new IllegalStateException("ShutdownHook method '%s' does not exist in class %s".formatted(methodName, node.name)));

			return new ClassVisitor(asmVersion, parent) {

				@Override
				public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
					final String[] newInterfaces = new String[interfaces.length + 1];
					System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
					newInterfaces[newInterfaces.length - 1] = ShutdownHookTransformer.IFACE;
					super.visit(version, access, name, signature, superName, newInterfaces);
				}

				@Override
				public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
					final MethodVisitor result = super.visitMethod(access, name, descriptor, signature, exceptions);
					if (name.equals("<init>")) {
						return new MethodVisitor(ASM9, result) {

							@Override
							public void visitMethodInsn(final int opcode, final String owner, final String name, final String descriptor, final boolean isInterface) {
								super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
								if (name.equals("<init>")) {

									this.visitVarInsn(ALOAD, 0);
									this.visitInsn(ICONST_1);
									this.visitMethodInsn(INVOKEINTERFACE, ShutdownHookTransformer.IFACE, ShutdownHookTransformer.IFACE_METHOD_2.getName(), Type.getMethodDescriptor(ShutdownHookTransformer.IFACE_METHOD_2),
											true);
								}
							}
						};
					}
					return result;
				}

				@Override
				public void visitEnd() {
					final MethodVisitor v = this.visitMethod(ACC_PUBLIC, ShutdownHookTransformer.IFACE_METHOD.getName(), ShutdownHookTransformer.IFACE_METHOD_DESC, null, null);
					v.visitCode();
					if ((methodNode.access & ACC_STATIC) == 0) {
						v.visitVarInsn(ALOAD, 0);
						v.visitMethodInsn(INVOKEVIRTUAL, node.name, methodName, methodNode.desc, false);
					} else {
						v.visitMethodInsn(INVOKESTATIC, node.name, methodName, methodNode.desc, false);
					}
					v.visitInsn(RETURN);
					v.visitEnd();
					super.visitEnd();
				}
			};
		});
	}
}
