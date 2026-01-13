package dawn.asset;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import org.joml.Vector4i;

public record Font(FontAsset asset, Texture texture, Char2ObjectMap<Vector4i> charData) {
}
