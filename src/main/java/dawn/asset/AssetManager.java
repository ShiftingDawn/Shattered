package dawn.asset;

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
	private final @Getter TextureManager textures = new TextureManager(this);

	public void init() {
		AssetManager.LOGGER.info("Reloading assets");
		this.textures.init();
	}
}
