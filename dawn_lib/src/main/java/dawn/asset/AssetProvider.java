package dawn.asset;

import dawn.Identifier;

public interface AssetProvider<VALUE> {

	VALUE get(Identifier identifier);
}
