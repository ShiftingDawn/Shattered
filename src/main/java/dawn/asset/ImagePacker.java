package dawn.asset;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import dawn.lib.math.Rectangle;
import dawn.registry.Identifier;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memSet;

final class ImagePacker implements AutoCloseable {

	private final @Getter Map<Identifier, Rectangle> elements = new HashMap<>();
	private final Node rootNode;
	private final @Getter ByteBuffer buffer;
	private final @Getter int atlasWidth;
	private final @Getter int atlasHeight;

	ImagePacker(final int atlasWidth, final int atlasHeight) {
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
		this.buffer = memAlloc(atlasWidth * atlasHeight * 4);
		for (int y = 0; y < atlasHeight; ++y) {
			memSet(memAddress(this.buffer), 0, atlasWidth * atlasHeight * 4L);
		}
		this.rootNode = new Node(atlasWidth, atlasHeight);
	}

	@Override
	public void close() {
		memFree(this.buffer);
	}

	void addImage(final Identifier resource, final ByteBuffer imageData, final int imageWidth, final int imageHeight) {
		if (this.elements.containsKey(resource)) {
			throw new RuntimeException("Key with Identifier '" + resource + "' is already in map");
		}
		Rectangle bounds = Rectangle.createMutable(0, 0, imageWidth, imageHeight);
		final Node imageNode = this.insertNode(this.rootNode, bounds);
		if (imageNode == null) {
			throw new RuntimeException("ATLAS_TOO_SMALL");
		}
		imageNode.resource = resource;
		bounds = imageNode.bounds.copy();
		this.elements.put(resource, bounds);
		for (int y = 0; y < imageHeight; ++y) {
			final long srcAddr = memAddress(imageData) + y * imageWidth * 4L;
			final long dstAddr = memAddress(this.buffer) + ((long) (bounds.getY() + y) * this.atlasWidth + bounds.getX()) * 4L;
			memCopy(srcAddr, dstAddr, imageWidth * 4L);
		}
	}

	@Nullable
	private Node insertNode(final Node node, final Rectangle bounds) {
		if (node.resource == null && node.leftChild != null && node.rightChild != null) {
			Node newNode = this.insertNode(node.leftChild, bounds);
			if (newNode == null) {
				newNode = this.insertNode(node.rightChild, bounds);
			}
			return newNode;
		} else {
			if (node.resource != null) {
				return null;
			}
			if (node.bounds.getSize().equals(bounds.getSize())) {
				return node;
			}
			if (node.bounds.getWidth() < bounds.getWidth() || node.bounds.getHeight() < bounds.getHeight()) {
				return null;
			}
			node.leftChild = new Node();
			node.rightChild = new Node();
			if (node.bounds.getWidth() - bounds.getWidth() > node.bounds.getHeight() - bounds.getHeight()) {
				node.leftChild.bounds = Rectangle.createMutable(node.bounds.getX(), node.bounds.getY(), bounds.getWidth(), node.bounds.getHeight());
				node.rightChild.bounds = Rectangle.createMutable(node.bounds.getX() + bounds.getWidth(), node.bounds.getY(), node.bounds.getWidth() - bounds.getWidth(), node.bounds.getHeight());
			} else {
				node.leftChild.bounds = Rectangle.createMutable(node.bounds.getX(), node.bounds.getY(), node.bounds.getWidth(), bounds.getHeight());
				node.rightChild.bounds = Rectangle.createMutable(node.bounds.getX(), node.bounds.getY() + bounds.getHeight(), node.bounds.getWidth(), node.bounds.getHeight() - bounds.getHeight());
			}
			return this.insertNode(node.leftChild, bounds);
		}
	}

	private static final class Node {

		private Rectangle bounds;
		private @Nullable Node leftChild = null;
		private @Nullable Node rightChild = null;
		private @Nullable Identifier resource = null;

		private Node(final int width, final int height) {
			this.bounds = Rectangle.createMutable(0, 0, width, height);
			this.leftChild = null;
			this.rightChild = null;
			this.resource = null;
		}

		private Node() {
			this.bounds = Rectangle.create(0, 0, 0, 0);
		}
	}
}