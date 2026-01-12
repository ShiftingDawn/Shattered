package dawn.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.gson.reflect.TypeToken;
import dawn.Dawn;
import dawn.asset.ResourceResolver;
import dawn.lib.ExitException;
import dawn.lib.RunOnce;
import dawn.lib.json.GsonHelper;

public final class RegistrySetup {

	private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();
	private static final RunOnce INITIALIZED = new RunOnce();
	private static final TypeToken<List<Identifier>> JSON_IDENTIFIER_LIST_TOKEN = new TypeToken<>() {};

	public static void load(final ResourceResolver resources) {
		RegistrySetup.INITIALIZED.test(() -> "Registries have already been initialized");
		Registries.init();
		try {
			final CompletableFuture<Void> future = RegistrySetup.loadRegistries(resources, Dawn.NAME_LOW);
			future.get();
		} catch (final IOException | InterruptedException | ExecutionException e) {
			Dawn.LOGGER.fatal("Could not load registry data", e);
			throw new ExitException();
		}
	}

	private static CompletableFuture<Void> loadRegistries(final ResourceResolver resources, final String domain) throws IOException, ExecutionException, InterruptedException {
		final List<CompletableFuture<Void>> futures = List.of(
			RegistrySetup.loadRegistry(resources, domain, Registries.SHADERS),
			RegistrySetup.loadRegistry(resources, domain, Registries.TEXTURES)
		);
		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	private static CompletableFuture<Void> loadRegistry(final ResourceResolver resources, final String domain, final Registry<?> registry) throws IOException {
		final Identifier registryIdentifier = Identifier.of(domain, registry.getRegistryName());
		final List<Identifier> content = RegistrySetup.readRegistryContent(resources, registryIdentifier);
		return CompletableFuture.runAsync(
			() -> ((RegistryImpl<?>) registry).loadContent(Dawn.getLogger("Registry{%s}".formatted(registryIdentifier)), resources, content),
			RegistrySetup.EXECUTOR_SERVICE
		);
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
