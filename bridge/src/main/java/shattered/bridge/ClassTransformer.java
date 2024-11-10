package shattered.bridge;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;

public interface ClassTransformer {

	@FunctionalInterface
	interface Applier {

		ClassVisitor apply(ClassNode node, int asmVersion, ClassVisitor parent);
	}

	default int priority() {
		return 0;
	}

	boolean canTransform(ClassNode node);

	byte[] transform(byte[] data);

	default byte[] handle(final byte[] input, final Applier applier) {
		final ClassReader reader = new ClassReader(input);
		final ClassNode node = new ClassNode();
		reader.accept(node, 0);
		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		final CheckClassAdapter adapter = new CheckClassAdapter(writer);
		node.accept(applier.apply(node, Opcodes.ASM9, adapter));
		return writer.toByteArray();
	}
}
