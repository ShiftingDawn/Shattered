package dawn.core.asset;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import dawn.Identifier;
import dawn.lib.Rectangle;
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
		Rectangle bounds = new Rectangle(0, 0, imageWidth, imageHeight);
		final Node imageNode = this.insertNode(this.rootNode, bounds);
		if (imageNode == null) {
			throw new RuntimeException("ATLAS_TOO_SMALL");
		}
		imageNode.resource = resource;
		bounds = imageNode.bounds.copy();
		this.elements.put(resource, bounds);
		for (int y = 0; y < imageHeight; ++y) {
			final long srcAddr = memAddress(imageData) + y * imageWidth * 4L;
			final long dstAddr = memAddress(this.buffer) + ((long) (bounds.y() + y) * this.atlasWidth + bounds.x()) * 4L;
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
			if (node.bounds.size().equals(bounds.size())) {
				return node;
			}
			if (node.bounds.w() < bounds.w() || node.bounds.h() < bounds.h()) {
				return null;
			}
			node.leftChild = new Node();
			node.rightChild = new Node();
			if (node.bounds.w() - bounds.w() > node.bounds.h() - bounds.h()) {
				node.leftChild.bounds = new Rectangle(node.bounds.x(), node.bounds.y(), bounds.w(), node.bounds.h());
				node.rightChild.bounds = new Rectangle(node.bounds.x() + bounds.w(), node.bounds.y(), node.bounds.w() - bounds.w(), node.bounds.h());
			} else {
				node.leftChild.bounds = new Rectangle(node.bounds.x(), node.bounds.y(), node.bounds.w(), bounds.h());
				node.rightChild.bounds = new Rectangle(node.bounds.x(), node.bounds.y() + bounds.h(), node.bounds.w(), node.bounds.h() - bounds.h());
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
			this.bounds = new Rectangle(0, 0, width, height);
			this.leftChild = null;
			this.rightChild = null;
			this.resource = null;
		}

		private Node() {
			this.bounds = new Rectangle(0, 0, 0, 0);
		}
	}
}