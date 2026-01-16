package dawn.core.lib.json;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dawn.Identifier;
import dawn.internal.DawnLib;
import dawn.lib.RunOnce;

public final class GsonFactory {

	private static final RunOnce INITIALIZED = new RunOnce();

	public static void init() {
		GsonFactory.INITIALIZED.test(() -> "GsonFactory has already been initialized");
		DawnLib.GSON = GsonFactory.makeGson();
		DawnLib.JSON_VALIDATOR = new JsonValidatorImpl();
		DawnLib.STRING_TYPE_VALIDATOR = new StringTypeValidatorImpl();
	}

	private static Gson makeGson() {
		return new GsonBuilder()
			//Properties
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.setExclusionStrategies(new AnnotatedExclusionStrategy())
			.serializeNulls()
			//Types
			.registerTypeAdapterFactory(new EnumAdapterFactory())
			.registerTypeAdapter(Identifier.class, new GsonIdentifierAdapter())
			//
			.create();
	}

	private GsonFactory() {
	}
}
