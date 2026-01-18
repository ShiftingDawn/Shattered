package dawn.dawndb;

import java.io.DataInput;
import java.io.IOException;

public interface DDBDeserializer<T extends DDBValue> {

	T deserialize(DataInput input) throws IOException;
}
