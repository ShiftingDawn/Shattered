package dawn.core.dawndb;

import java.io.DataInput;
import java.io.IOException;
import dawn.dawndb.DawnDB;

final class DDBImpl extends DDBTableImpl implements DawnDB {

	public void load(final DataInput input) throws IOException {
		DDBTableImpl.deserialize(input, this);
	}
}
