package dawn.asset;

import dawn.registry.RegistryObject;

public interface ProtoAsset<PROTO extends RegistryObject> {

	PROTO proto();
}
