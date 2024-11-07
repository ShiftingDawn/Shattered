package shattered.core.event;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;
import shattered.bridge.ClassTransformer;
import shattered.lib.event.EventBusSubscriber;

final class EventBusSubscriberClassTransformer implements ClassTransformer {

	private static final String DESC = Type.getDescriptor(EventBusSubscriber.class);

	@Override
	public boolean canTransform(ClassNode node) {
		return node.visibleAnnotations.stream().anyMatch(annotationNode -> annotationNode.desc.equals(DESC));
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
		});
		return writer.toByteArray();
	}
}
