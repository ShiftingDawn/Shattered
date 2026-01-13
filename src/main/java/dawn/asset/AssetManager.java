package dawn.asset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import dawn.Dawn;
import dawn.lib.Workspace;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;

@RequiredArgsConstructor
public final class AssetManager {

	private static final Logger LOGGER = Dawn.getLogger("Assets");
	private final @Getter ResourceResolver resources;
	private final @Getter Workspace workspace;
	private final @Getter ShaderManager shaders = new ShaderManager(this);
	private final @Getter TextureManager textures = new TextureManager(this);
	private final @Getter FontManager fonts = new FontManager(this);

	public void init() {
		AssetManager.LOGGER.info("Reloading assets");
		this.shaders.init();
		this.textures.init();
		this.fonts.init();
	}

	public void dumpAsset(final String path, final ByteBuffer data) {
		final File file = this.workspace.getTempFile(path);
		file.getParentFile().mkdirs();
		try (FileOutputStream foas = new FileOutputStream(file)) {
			final byte[] bytes = new byte[data.capacity()];
			data.get(bytes);
			foas.write(bytes);
		} catch (final IOException e) {
			AssetManager.LOGGER.error("Could not dump asset with path: {}", file.getAbsoluteFile(), e);
		}
	}
}
