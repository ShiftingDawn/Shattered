package shattered.core.event;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;
import shattered.bridge.ClassTransformer;
import shattered.lib.event.EventBusSubscriber;

final class EventBusSubscriberClassTransformer implements ClassTransformer {

	private static final String DESC = Type.getDescriptor(EventBusSubscriber.class);

	@Override
	public boolean canTransform(final ClassNode node) {
		return node.visibleAnnotations != null && node.visibleAnnotations.stream().anyMatch(annotationNode -> annotationNode.desc.equals(EventBusSubscriberClassTransformer.DESC));
	}

	@Override
	public byte[] transform(final byte[] data) {
		final ClassReader reader = new ClassReader(data);
		final ClassNode node = new ClassNode();
		reader.accept(node, 0);

		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		final CheckClassAdapter adapter = new CheckClassAdapter(writer);
		node.accept(new ClassVisitor(Opcodes.ASM9, adapter) {

			@Override
			public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
				final int newAccess = (access & ~Opcodes.ACC_PRIVATE & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC;
				super.visit(version, newAccess, name, signature, superName, interfaces);
			}
		});
		return writer.toByteArray();
	}
}
