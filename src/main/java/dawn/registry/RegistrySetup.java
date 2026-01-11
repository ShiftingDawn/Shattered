package dawn.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import dawn.Dawn;
import dawn.asset.ResourceResolver;
import dawn.lib.ExitException;
import dawn.lib.RunOnce;
import dawn.lib.json.GsonHelper;

public final class RegistrySetup {

	private static final RunOnce INITIALIZED = new RunOnce();
	private static final TypeToken<List<Identifier>> JSON_IDENTIFIER_LIST_TOKEN = new TypeToken<>() {};

	public static void load(final ResourceResolver resources) {
		RegistrySetup.INITIALIZED.test(() -> "Registries have already been initialized");
		Registries.init();
		try {
			RegistrySetup.loadRegistries(resources, Dawn.NAME_LOW);
		} catch (final IOException e) {
			Dawn.LOGGER.fatal("Could not load registry data", e);
			throw new ExitException();
		}
	}

	private static void loadRegistries(final ResourceResolver resources, final String domain) throws IOException {
		RegistrySetup.loadRegistry(resources, domain, Registries.TEXTURES);
	}

	private static void loadRegistry(final ResourceResolver resources, final String domain, final Registry<?> registry) throws IOException {
		final Identifier registryIdentifier = Identifier.of(domain, registry.getRegistryName());
		final List<Identifier> content = RegistrySetup.readRegistryContent(resources, registryIdentifier);
		((RegistryImpl<?>) registry).loadContent(Dawn.getLogger("Registry{%s}".formatted(registryIdentifier)), resources, content);
	}

	private static List<Identifier> readRegistryContent(final ResourceResolver resources, final Identifier registry) throws IOException {
		final String path = resources.makePath(registry, null, "json");
		try (InputStream stream = resources.getStream(path)) {
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
