package dawn.asset;

import dawn.registry.Identifier;
import dawn.registry.RegistryObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class ShaderAsset implements RegistryObject {

	private final Identifier registryKey;

	private final boolean canTexture;
	private final boolean canColor;

	private final String propOutColor;
	private final String propMatrixProjection;
	private final String propMatrixModelView;
	private final String propMatrixTessellatorTransform;

	private final String propEnableTexture;
}
