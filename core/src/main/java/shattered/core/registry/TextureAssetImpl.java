package shattered.core.registry;

import shattered.lib.registry.Identifier;
import shattered.lib.resource.TextureAsset;

public abstract class TextureAssetImpl extends TextureAsset {

	public TextureAssetImpl(final Identifier registryKey) {
		super(registryKey);
	}

	public static final class Default extends TextureAssetImpl {

		public Default(final Identifier registryKey) {
			super(registryKey);
		}
	}

	public static final class Stitched extends TextureAssetImpl {

		public final int spriteCount;
		public final int spriteWidth;
		public final int spriteHeight;

		public Stitched(final Identifier registryKey, final int spriteCount, final int spriteWidth, final int spriteHeight) {
			super(registryKey);
			this.spriteCount = spriteCount;
			this.spriteWidth = spriteWidth;
			this.spriteHeight = spriteHeight;
		}
	}

	public static final class Bordered extends TextureAssetImpl {

		public final int borderTop;
		public final int borderBottom;
		public final int borderLeft;
		public final int borderRight;

		public Bordered(final Identifier registryKey, final int borderTop, final int borderBottom, final int borderLeft, final int borderRight) {
			super(registryKey);
			this.borderTop = borderTop;
			this.borderBottom = borderBottom;
			this.borderLeft = borderLeft;
			this.borderRight = borderRight;
		}
	}

	public static final class Animation extends TextureAssetImpl {

		public final double fps;
		public final Integer[] frameMapping;

		public Animation(final Identifier registryKey, final double fps, final Integer[] frameMapping) {
			super(registryKey);
			this.fps = fps;
			this.frameMapping = frameMapping;
		}
	}
}
