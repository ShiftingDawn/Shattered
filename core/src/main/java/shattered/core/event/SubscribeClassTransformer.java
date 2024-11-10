package shattered.core.event;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import shattered.bridge.ClassTransformer;
import shattered.lib.event.Subscribe;

final class SubscribeClassTransformer implements ClassTransformer {

	private static final String DESC = Type.getDescriptor(Subscribe.class);

	@Override
	public boolean canTransform(final ClassNode node) {
		return node.methods.stream().anyMatch(m -> m.visibleAnnotations != null && m.visibleAnnotations.stream().anyMatch(a -> a.desc.equals(SubscribeClassTransformer.DESC)));
	}

	@Override
	public byte[] transform(final byte[] data) {
		return this.handle(data, (node, asmVersion, parent) -> new ClassVisitor(Opcodes.ASM9, parent) {

			@Override
			public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
				final int newAccess = (access & ~Opcodes.ACC_PRIVATE & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC;
				super.visit(version, newAccess, name, signature, superName, interfaces);
			}

			@Override
			public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
				for (final var methodNode : node.methods) {
					if (methodNode.name.equals(name) && methodNode.desc.equals(descriptor)) {
						final int newAccess = (access & ~Opcodes.ACC_PRIVATE & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC;
						return super.visitMethod(newAccess, name, descriptor, signature, exceptions);
					}
				}
				return super.visitMethod(access, name, descriptor, signature, exceptions);
			}
		});
	}
}
