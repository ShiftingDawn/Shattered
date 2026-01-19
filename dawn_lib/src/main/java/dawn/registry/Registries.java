package dawn.registry;

import dawn.internal.DawnLib;

public interface Registries {

	static Registries get() {
		return DawnLib.REGISTRIES;
	}

	Registry<ProtoShader> shaders();

	Registry<ProtoTexture> textures();

	Registry<ProtoFont> fonts();

	Registry<ProtoLanguage> languages();
}
