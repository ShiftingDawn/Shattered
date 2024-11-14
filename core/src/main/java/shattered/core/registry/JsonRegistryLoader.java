package shattered.core.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import shattered.Shattered;
import shattered.lib.registry.Identifier;
import shattered.lib.registry.Registries;
import shattered.lib.util.Utils;
import shattered.lib.util.gson.GsonHelper;

public final class JsonRegistryLoader {

	public static final TypeToken<List<Identifier>> IDENTIFIER_LIST_TYPE = new TypeToken<>() {

	};

	public static void createRegistries() {
		Registries.TEXTURE = new RegistryImpl<>("texture", new TextureRegistryContentFactory());
	}

	public static void loadRegistries() throws IOException {
		JsonRegistryLoader.loadRegistries(Identifier.DEFAULT_NAMESPACE);
	}

	private static void loadRegistries(final String namespace) throws IOException {
		Utils.make(JsonRegistryLoader.readJsonList(Identifier.of(namespace, Registries.TEXTURE.getRegistryName())), ((RegistryImpl<?>) Registries.TEXTURE)::loadContent);
	}

	public static List<Identifier> readJsonList(final Identifier registryIdentifier) throws IOException {
		final String path = "/assets/%s/%s.json".formatted(registryIdentifier.getNamespace(), registryIdentifier.getPath());
		try (InputStream stream = Shattered.class.getResourceAsStream(path)) {
			if (stream == null) {
				throw new FileNotFoundException("Could not load resource: " + path);
			}
			return GsonHelper.GSON.fromJson(new InputStreamReader(stream), JsonRegistryLoader.IDENTIFIER_LIST_TYPE);
		}
	}

	private JsonRegistryLoader() {
	}
}
