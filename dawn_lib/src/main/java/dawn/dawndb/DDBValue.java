package dawn.dawndb;

import java.io.DataOutput;
import java.io.IOException;
import dawn.Identifier;

public interface DDBValue {

	Identifier getId();

	void serialize(DataOutput output) throws IOException;
}
