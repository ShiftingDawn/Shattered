package dawn.core.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dawn.Identifier;
import dawn.core.DawnImpl;
import dawn.core.ExitException;
import dawn.core.lib.json.GsonIdentifierAdapter;
import dawn.lib.ResourceFinder;
import dawn.lib.RunOnce;
import dawn.registry.ProtoAudio;
import dawn.registry.ProtoFont;
import dawn.registry.ProtoLanguage;
import dawn.registry.ProtoShader;
import dawn.registry.ProtoTexture;
import dawn.registry.Registries;
import dawn.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RegistriesImpl implements Registries {

	private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();
	private static final RunOnce INITIALIZED = new RunOnce();
	private static final Gson LOADER_GSON;
	private static final TypeToken<List<Identifier>> JSON_IDENTIFIER_LIST_TOKEN = new TypeToken<>() {};

	private static final RegistryImpl<ProtoShader> SHADERS = new RegistryImpl<>("shader", new ProtoShaderContentFactory());
	private static final RegistryImpl<ProtoTexture> TEXTURES = new RegistryImpl<>("texture", new ProtoTextureContentFactory());
	private static final RegistryImpl<ProtoFont> FONTS = new RegistryImpl<>("font", new ProtoFontContentFactory());
	private static final RegistryImpl<ProtoAudio> AUDIO = new RegistryImpl<>("audio", new ProtoAudioContentFactory());
	private static final RegistryImpl<ProtoLanguage> LANGUAGES = new RegistryImpl<>("language", new ProtoLanguageContentFactory());

	public static void load(final ResourceFinder resources) {
		RegistriesImpl.INITIALIZED.test(() -> "Registries have already been initialized");
		RegistriesImpl.loadAll(resources, Identifier.DEFAULT_DOMAIN).join();
	}

	@Override
	public Registry<ProtoShader> shaders() {
		return RegistriesImpl.SHADERS;
	}

	@Override
	public Registry<ProtoTexture> textures() {
		return RegistriesImpl.TEXTURES;
	}

	@Override
	public Registry<ProtoFont> fonts() {
		return RegistriesImpl.FONTS;
	}

	@Override
	public Registry<ProtoAudio> audio() {
		return RegistriesImpl.AUDIO;
	}

	@Override
	public Registry<ProtoLanguage> languages() {
		return RegistriesImpl.LANGUAGES;
	}

	private static CompletableFuture<Void> loadAll(final ResourceFinder resources, final String domain) {
		return CompletableFuture.allOf(Stream.of(
			RegistriesImpl.SHADERS, RegistriesImpl.TEXTURES, RegistriesImpl.FONTS, RegistriesImpl.AUDIO, RegistriesImpl.LANGUAGES
		).map(reg -> RegistriesImpl.loadRegistry(resources, domain, reg)).toList().toArray(CompletableFuture[]::new));
	}

	private static CompletableFuture<Void> loadRegistry(final ResourceFinder resources, final String domain, final RegistryImpl<?> registry) {
		final Identifier registryIdentifier = Identifier.of(domain, registry.getRegistryName());
		return CompletableFuture.runAsync(() -> {
			try {
				final Logger logger = LogManager.getLogger("Registry{%s}".formatted(registryIdentifier));
				final List<Identifier> content = RegistriesImpl.readRegistryContent(resources, registryIdentifier);
				CompletableFuture.allOf(content.stream().map(
					id -> registry.loadContent(RegistriesImpl.EXECUTOR_SERVICE, logger, resources, id)
				).toArray(CompletableFuture[]::new)).join();
			} catch (final IOException e) {
				DawnImpl.LOGGER.fatal("Could not load registry data", e);
				throw new ExitException();
			}
		}, RegistriesImpl.EXECUTOR_SERVICE);
	}

	private static List<Identifier> readRegistryContent(final ResourceFinder resources, final Identifier registry) throws IOException {
		final List<Identifier> result = new ArrayList<>();
		final String path = resources.makePath(registry, null, "json");
		final var urls = resources.getResources(path);
		for (final URL url : urls) {
			try (InputStream stream = url.openStream()) {
				if (stream == null) {
					throw new FileNotFoundException("Could not load registry file. Expected path: " + path);
				}
				final List<Identifier> ids = RegistriesImpl.LOADER_GSON.fromJson(new InputStreamReader(stream), RegistriesImpl.JSON_IDENTIFIER_LIST_TOKEN);
				result.addAll(ids);
			}
		}
		return result;
	}

	static {
		LOADER_GSON = new GsonBuilder().registerTypeAdapter(Identifier.class, new GsonIdentifierAdapter()).create();
	}
}
