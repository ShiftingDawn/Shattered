package dawn.asset;

import dawn.registry.Identifier;
import dawn.registry.RegistryObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class FontAsset implements RegistryObject {

	private final @Getter Identifier registryKey;
}
