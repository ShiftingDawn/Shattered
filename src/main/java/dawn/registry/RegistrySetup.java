package dawn.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import dawn.Dawn;
import dawn.asset.AssetResolver;
import dawn.lib.ExitException;
import dawn.lib.RunOnce;
import dawn.lib.json.GsonHelper;

public final class RegistrySetup {

	private static final RunOnce INITIALIZED = new RunOnce();
	private static final TypeToken<List<Identifier>> JSON_IDENTIFIER_LIST_TOKEN = new TypeToken<>() {};

	public static void load(final AssetResolver assets) {
		RegistrySetup.INITIALIZED.test(() -> "Registries have already been initialized");
		Registries.init();
		try {
			RegistrySetup.loadRegistries(assets, Dawn.NAME_LOW);
		} catch (final IOException e) {
			Dawn.LOGGER.fatal("Could not load registry data", e);
			throw new ExitException();
		}
	}

	private static void loadRegistries(final AssetResolver assets, final String domain) throws IOException {
		RegistrySetup.loadRegistry(assets, domain, Registries.TEXTURES);
	}

	private static void loadRegistry(final AssetResolver assets, final String domain, final Registry<?> registry) throws IOException {
		final List<Identifier> content = RegistrySetup.readRegistryContent(assets, Identifier.of(domain, registry.getRegistryName()));
		((RegistryImpl<?>) registry).loadContent(assets, content);
	}

	private static List<Identifier> readRegistryContent(final AssetResolver assets, final Identifier registry) throws IOException {
		final String path = assets.makePath(registry, null, "json");
		try (InputStream stream = assets.getStream(path)) {
			if (stream == null) {
				throw new FileNotFoundException("Could not load registry file. Expected path: " + path);
			}
			return GsonHelper.GSON.fromJson(new InputStreamReader(stream), RegistrySetup.JSON_IDENTIFIER_LIST_TOKEN);
		}
	}

	static <T extends RegistryObject> Registry<T> makeRegistry(final String registryName, final RegistryContentFactory<T> contentFactory) {
		return new RegistryImpl<>(registryName, contentFactory);
	}

	private RegistrySetup() {
	}
}
