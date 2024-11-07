package shattered.bootstrap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import shattered.bridge.ClassTransformer;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.TreeSet;

public final class TransformerRegistry {

    private static final Set<ClassTransformer> TRANSFORMERS = new TreeSet<>((o1, o2) -> Integer.compare(o2.priority(), o1.priority()));

    static byte[] transform(final String className, final byte[] data) {
        byte[] newData = data;
        ClassReader currentReader = new ClassReader(data);
        ClassNode currentNode = new ClassNode(Opcodes.ASM9);
        currentReader.accept(currentNode, 0);
        for (final ClassTransformer transformer : TransformerRegistry.TRANSFORMERS) {
            if (!transformer.canTransform(currentNode)) {
                continue;
            }
            final byte[] transformed = transformer.transform(newData);
            if (transformed != null && transformed.length > 0) {
                try {
                    currentReader = new ClassReader(transformed);
                    currentNode = new ClassNode(Opcodes.ASM9);
                    currentReader.accept(currentNode, 0);
                    newData = transformed;
                } catch (final Exception e) {
                    System.err.printf("Could not apply transformer %s to class %s%n", transformer.getClass().getName(), className);
                    e.printStackTrace();
                }
            }
        }
        return newData;
    }

    static void registerTransformer(final String className, final ClassNode node) {
        if (!node.superName.equals(Type.getInternalName(Object.class)) || node.interfaces.size() != 1) {
            throw new BootstrapException("ClassTransformer %s is invalid".formatted(className));
        }
        try {
            final Class<?> clazz = Class.forName(className);
            final Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            final Object instance = constructor.newInstance();
            TransformerRegistry.TRANSFORMERS.add((ClassTransformer) instance);
        } catch (final ClassNotFoundException e) {
            throw new BootstrapException();
        } catch (Throwable e) {
            throw new BootstrapException("ClassTransformer %s is invalid".formatted(className));
        }
    }
}
