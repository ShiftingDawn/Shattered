package dawn.gfx;

import dawn.Identifier;
import dawn.asset.Texture;
import org.jspecify.annotations.Nullable;

public interface TextureAtlas extends Texture {

	@Nullable Texture getTexture(Identifier texture);
}
