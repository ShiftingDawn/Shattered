package shattered.lib.util.gson;

import java.io.IOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import shattered.lib.registry.Identifier;

final class GsonIdentifierAdapter extends TypeAdapter<Identifier> {

	public static final GsonIdentifierAdapter INSTANCE = new GsonIdentifierAdapter();

	@Override
	public void write(final JsonWriter out, final Identifier value) throws IOException {
		out.value(value.toString());
	}

	@Override
	public Identifier read(final JsonReader in) throws IOException {
		return Identifier.of(in.nextString());
	}
}
