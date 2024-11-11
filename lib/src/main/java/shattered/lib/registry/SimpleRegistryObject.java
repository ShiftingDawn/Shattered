package shattered.lib.registry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class SimpleRegistryObject implements RegistryObject {

	@Getter
	private final Identifier registryKey;
}
