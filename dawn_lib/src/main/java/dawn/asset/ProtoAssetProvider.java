package dawn.asset;

import dawn.registry.RegistryObject;

public interface ProtoAssetProvider<PROTO extends RegistryObject, VALUE extends ProtoAsset<PROTO>> extends AssetProvider<VALUE> {

	default VALUE get(final PROTO proto) {
		return this.get(proto.getRegistryKey());
	}
}
