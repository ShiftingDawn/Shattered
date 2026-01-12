package dawn.asset;

import java.util.concurrent.ConcurrentHashMap;
import dawn.Dawn;
import dawn.gfx.Shader;
import dawn.registry.Identifier;
import dawn.registry.Registries;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import static org.lwjgl.opengl.GL20.glDeleteProgram;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ShaderManager {

	private static final Logger LOGGER = Dawn.getLogger("Shaders");
	private final ConcurrentHashMap<Identifier, Shader> mapping = new ConcurrentHashMap<>();
	private final AssetManager assets;

	void init() {
		ShaderManager.LOGGER.info("Reloading shaders");
		for (final ShaderAsset shader : Registries.SHADERS) {
			this.unloadShader(shader);
			this.loadShader(shader);
		}
	}

	private void loadShader(final ShaderAsset shader) {
		ShaderManager.LOGGER.debug("\tLoading shader {}", shader.getRegistryKey());
		if (this.mapping.containsKey(shader.getRegistryKey())) {
			ShaderManager.LOGGER.error("\tTrying to load duplicate shader {}. This will most likely result in a memory leak.", shader.getRegistryKey());
		}
		final Shader program = new Shader(this.assets.getResources(), shader);
		this.mapping.put(shader.getRegistryKey(), program);
		ShaderManager.LOGGER.debug("\t\tDone");
	}

	private void unloadShader(final ShaderAsset shader) {
		final Shader program = this.mapping.get(shader.getRegistryKey());
		if (program != null) {
			ShaderManager.LOGGER.debug("\tUnloading shader {} ({})", shader.getRegistryKey(), program.getProgram());
			glDeleteProgram(program.getProgram());
			this.mapping.remove(shader.getRegistryKey());
			ShaderManager.LOGGER.debug("\t\tDone");
		}
	}

	public Shader getShader(final Identifier shader) {
		return this.mapping.get(shader);
	}

	public Shader getShader(final ShaderAsset shader) {
		return this.getShader(shader.getRegistryKey());
	}
}
