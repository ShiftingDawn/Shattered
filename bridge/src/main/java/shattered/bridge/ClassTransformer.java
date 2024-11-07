package shattered.bridge;

import org.objectweb.asm.tree.ClassNode;

public interface ClassTransformer {

	default int priority() {
		return 0;
	}

	boolean canTransform(ClassNode node);

	byte[] transform(byte[] data);
}
