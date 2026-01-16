package dawn.core.asset;

import java.util.Map;
import dawn.Dawn;
import dawn.Identifier;
import dawn.asset.Texture;
import dawn.gfx.TextureAtlas;
import dawn.lib.Rectangle;
import dawn.registry.ProtoTexture;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

final class TextureAtlasImpl implements TextureAtlas, Texture {

	private final Texture texture;
	private final Map<Identifier, AtlasTexture> textures;

	public TextureAtlasImpl(final Texture texture, final Map<Identifier, Rectangle> elements, final Map<Identifier, ProtoTexture> entries) {
		this.texture = texture;
		this.textures = Object2ObjectMaps.unmodifiable(Dawn.make(new Object2ObjectArrayMap<>(), map -> {
			for (final Identifier entry : elements.keySet()) {
				final Rectangle bounds = elements.get(entry);
				final int[] uv = new int[] { bounds.x(), bounds.y(), bounds.w(), bounds.h() };
				map.put(entry, new AtlasTexture(this, entries.get(entry), bounds.w(), bounds.h(), uv));
			}
		}));
	}

	@Override
	public @Nullable Texture getTexture(final Identifier texture) {
		return this.textures.get(texture);
	}

	@Override
	public ProtoTexture proto() {
		return this.texture.proto();
	}

	@Override
	public int id() {
		return this.texture.id();
	}

	@Override
	public int width() {
		return this.texture.width();
	}

	@Override
	public int height() {
		return this.texture.height();
	}

	@Override
	public int imageWidth() {
		return this.width();
	}

	@Override
	public int imageHeight() {
		return this.height();
	}

	@Override
	public int u0() {
		return this.texture.u0();
	}

	@Override
	public int v0() {
		return this.texture.v0();
	}

	@Override
	public int u1() {
		return this.texture.u1();
	}

	@Override
	public int v1() {
		return this.texture.v1();
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static class AtlasTexture implements Texture {

		private final TextureAtlasImpl atlas;
		private final ProtoTexture proto;
		private final int width;
		private final int height;
		private final int[] uv;

		@Override
		public ProtoTexture proto() {
			return this.proto;
		}

		@Override
		public int id() {
			return this.atlas.id();
		}

		@Override
		public int width() {
			return this.width;
		}

		@Override
		public int height() {
			return this.height;
		}

		@Override
		public int imageWidth() {
			return this.atlas.imageWidth();
		}

		@Override
		public int imageHeight() {
			return this.atlas.imageHeight();
		}

		@Override
		public int u0() {
			return this.uv[0];
		}

		@Override
		public int v0() {
			return this.uv[1];
		}

		@Override
		public int u1() {
			return this.uv[2];
		}

		@Override
		public int v1() {
			return this.uv[3];
		}
	}
}
