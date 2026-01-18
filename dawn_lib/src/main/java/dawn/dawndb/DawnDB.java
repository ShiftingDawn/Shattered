package dawn.dawndb;

import java.io.IOException;
import dawn.Identifier;
import dawn.internal.DawnLib;

public interface DawnDB extends DDBTable {

	static DawnDB newDB() {
		return DawnLib.DDB_HELPER.newDB();
	}

	static DawnDB load(final String filename) throws IOException {
		return DawnLib.DDB_HELPER.loadDB(filename);
	}

	default void save(final String filename) throws IOException {
		DawnLib.DDB_HELPER.storeDB(this, filename);
	}

	static void addDeserializer(final Identifier identifier, final DDBDeserializer<?> deserializer) {
		DawnLib.DDB_HELPER.addDeserializer(identifier, deserializer);
	}
}
