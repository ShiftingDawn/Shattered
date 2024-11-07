package shattered.core.event;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;
import shattered.bridge.ClassTransformer;
import shattered.lib.event.EventBusSubscriber;
import shattered.lib.event.Subscribe;

final class SubscribeClassTransformer implements ClassTransformer {

	private static final String DESC = Type.getDescriptor(Subscribe.class);

	@Override
	public boolean canTransform(ClassNode node) {
		return node.methods.stream().anyMatch(m -> m.visibleAnnotations.stream().anyMatch(a -> a.desc.equals(DESC)));
	}

	@Override
	public byte[] transform(byte[] data) {
		ClassReader reader = new ClassReader(data);
		ClassNode node = new ClassNode();
		reader.accept(node, 0);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		CheckClassAdapter adapter = new CheckClassAdapter(writer);
		node.accept(new ClassVisitor(Opcodes.ASM9, adapter) {
			@Override
			public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
				int newAccess = (access & ~Opcodes.ACC_PRIVATE & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC;
				super.visit(version, newAccess, name, signature, superName, interfaces);
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
				for (var methodNode : node.methods) {
					if (methodNode.name.equals(name) && methodNode.desc.equals(descriptor)) {
						int newAccess = (access & ~Opcodes.ACC_PRIVATE & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC;
						return super.visitMethod(newAccess, name, descriptor, signature, exceptions);
					}
				}
				return super.visitMethod(access, name, descriptor, signature, exceptions);
			}
		});
		return writer.toByteArray();
	}
}
