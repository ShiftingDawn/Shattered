package dawn.core.asset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import dawn.asset.AssetManager;
import dawn.asset.Font;
import dawn.asset.ProtoAssetProvider;
import dawn.asset.Shader;
import dawn.asset.Texture;
import dawn.core.DawnImpl;
import dawn.lib.ResourceFinder;
import dawn.lib.Workspace;
import dawn.registry.ProtoFont;
import dawn.registry.ProtoShader;
import dawn.registry.ProtoTexture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class AssetManagerImpl implements AssetManager {

	private final @Getter ResourceFinder resources;
	private final @Getter Workspace workspace;
	private final ShaderManager shaders = new ShaderManager(this);
	private final TextureManager textures = new TextureManager(this);
	private final FontManager fonts = new FontManager(this);

	public void init() {
		DawnImpl.LOGGER.info("Reloading assets");
		this.shaders.init();
		this.textures.init();
		this.fonts.init();
	}

	@Override
	public ProtoAssetProvider<ProtoShader, Shader> shaders() {
		return this.shaders;
	}

	@Override
	public ProtoAssetProvider<ProtoTexture, Texture> textures() {
		return this.textures;
	}

	@Override
	public ProtoAssetProvider<ProtoFont, Font> fonts() {
		return this.fonts;
	}

	public void dumpAsset(final String path, final ByteBuffer data) {
		final File file = this.workspace.getTempFile(path);
		file.getParentFile().mkdirs();
		try (FileOutputStream foas = new FileOutputStream(file)) {
			final byte[] bytes = new byte[data.capacity()];
			data.get(bytes);
			foas.write(bytes);
		} catch (final IOException e) {
			DawnImpl.LOGGER.error("Could not dump asset with path: {}", file.getAbsoluteFile(), e);
		}
	}
}
