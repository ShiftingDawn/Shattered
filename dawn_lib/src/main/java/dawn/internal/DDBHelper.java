package dawn.internal;

import java.io.IOException;
import dawn.Identifier;
import dawn.dawndb.DDBDeserializer;
import dawn.dawndb.DawnDB;

public interface DDBHelper {

	DawnDB newDB();

	DawnDB loadDB(String filename) throws IOException;

	void storeDB(DawnDB db, String filename) throws IOException;

	void addDeserializer(Identifier identifier, DDBDeserializer<?> deserializer);
}
