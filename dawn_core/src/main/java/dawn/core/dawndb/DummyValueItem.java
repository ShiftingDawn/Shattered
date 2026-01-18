package dawn.core.dawndb;

import java.io.DataOutput;
import java.io.IOException;
import dawn.Identifier;
import dawn.dawndb.DDBValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class DummyValueItem implements DDBValue {

	private final Identifier id;
	private final byte[] data;

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public void serialize(final DataOutput output) throws IOException {
		DDBHelperImpl.writeString(output, this.id.toString());
		output.writeInt(this.data.length);
		output.write(this.data);
	}
}
