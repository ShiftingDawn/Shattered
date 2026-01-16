package dawn.asset;

import dawn.registry.ProtoShader;

public interface Shader extends ProtoAsset<ProtoShader> {

	int getProgram();
}
