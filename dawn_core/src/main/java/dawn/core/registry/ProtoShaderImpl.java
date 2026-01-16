package dawn.core.registry;

import dawn.Identifier;
import dawn.registry.ProtoShader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
final class ProtoShaderImpl implements ProtoShader {

	private final Identifier registryKey;

	private final boolean canTexture;
	private final boolean canColor;

	private final String propOutColor;
	private final String propMatrixProjection;
	private final String propMatrixModelView;
	private final String propMatrixTessellatorTransform;

	private final String propEnableTexture;
}
