package dawn.core.asset;

import java.util.concurrent.ConcurrentHashMap;
import dawn.Identifier;
import dawn.asset.ProtoAssetProvider;
import dawn.asset.Shader;
import dawn.registry.ProtoShader;
import dawn.registry.Registries;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.lwjgl.opengl.GL20.glDeleteProgram;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ShaderManager implements ProtoAssetProvider<ProtoShader, Shader> {

	private static final Logger LOGGER = LogManager.getLogger("Shaders");
	private final ConcurrentHashMap<Identifier, Shader> mapping = new ConcurrentHashMap<>();
	private final AssetManagerImpl assets;

	public void init() {
		ShaderManager.LOGGER.info("Reloading shaders");
		for (final ProtoShader shader : Registries.get().shaders()) {
			this.unloadShader(shader);
			this.loadShader(shader);
		}
	}

	private void loadShader(final ProtoShader shader) {
		ShaderManager.LOGGER.debug("\tLoading shader {}", shader.getRegistryKey());
		if (this.mapping.containsKey(shader.getRegistryKey())) {
			ShaderManager.LOGGER.error("\tTrying to load duplicate shader {}. This will most likely result in a memory leak.", shader.getRegistryKey());
		}
		final Shader program = new ShaderImpl(this.assets.getResources(), shader);
		this.mapping.put(shader.getRegistryKey(), program);
		ShaderManager.LOGGER.debug("\t\tDone");
	}

	private void unloadShader(final ProtoShader shader) {
		final Shader program = this.mapping.get(shader.getRegistryKey());
		if (program != null) {
			ShaderManager.LOGGER.debug("\tUnloading shader {} ({})", shader.getRegistryKey(), program.getProgram());
			glDeleteProgram(program.getProgram());
			this.mapping.remove(shader.getRegistryKey());
			ShaderManager.LOGGER.debug("\t\tDone");
		}
	}

	@Override
	public Shader get(final Identifier identifier) {
		return this.mapping.get(identifier);
	}
}
