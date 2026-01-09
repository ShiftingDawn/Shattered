package dawn.lib;

import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dawn.registry.Identifier;

public final class Json {

    public static final Gson GSON;

    static {
        GSON = new GsonBuilder()
	        .registerTypeAdapter(Identifier.class, new IdentifierJsonAdapter())
	        .setStrictness(Strictness.LENIENT)
	        .create();
    }

	private static class IdentifierJsonAdapter extends TypeAdapter<Identifier> {

		@Override
		public void write(JsonWriter out, Identifier value) throws IOException {
			out.jsonValue(value.toString());
		}

		@Override
		public Identifier read(JsonReader in) throws IOException {
			return Identifier.of(in.nextString());
		}
	}

	private Json() {
	}
}
