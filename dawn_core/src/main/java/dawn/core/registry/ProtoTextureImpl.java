package dawn.core.registry;

import dawn.Identifier;
import dawn.registry.ProtoTexture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class ProtoTextureImpl implements ProtoTexture {

	private final @Getter Identifier registryKey;

	public static final class DefaultImpl extends ProtoTextureImpl implements Default {

		public DefaultImpl(final Identifier registryKey) {
			super(registryKey);
		}
	}

	public static final class StitchedImpl extends ProtoTextureImpl implements Stitched {

		private final @Getter int spriteCount;
		private final @Getter int spriteWidth;
		private final @Getter int spriteHeight;

		public StitchedImpl(final Identifier registryKey, final int spriteCount, final int spriteWidth, final int spriteHeight) {
			super(registryKey);
			this.spriteCount = spriteCount;
			this.spriteWidth = spriteWidth;
			this.spriteHeight = spriteHeight;
		}
	}

	public static final class BorderedImpl extends ProtoTextureImpl implements Bordered {

		private final @Getter int borderTop;
		private final @Getter int borderBottom;
		private final @Getter int borderLeft;
		private final @Getter int borderRight;

		public BorderedImpl(final Identifier registryKey, final int borderTop, final int borderBottom, final int borderLeft, final int borderRight) {
			super(registryKey);
			this.borderTop = borderTop;
			this.borderBottom = borderBottom;
			this.borderLeft = borderLeft;
			this.borderRight = borderRight;
		}
	}

	public static final class AnimationImpl extends ProtoTextureImpl implements Animation {

		private final @Getter double fps;
		private final @Getter Integer[] frameMapping;

		public AnimationImpl(final Identifier registryKey, final double fps, final Integer[] frameMapping) {
			super(registryKey);
			this.fps = fps;
			this.frameMapping = frameMapping;
		}
	}
}
