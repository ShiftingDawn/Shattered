package dawn.core.dawndb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import dawn.Identifier;
import dawn.dawndb.DDBArray;
import dawn.dawndb.DDBDeserializer;
import dawn.dawndb.DDBTable;
import dawn.dawndb.DDBValue;
import dawn.dawndb.DawnDB;
import dawn.internal.DDBHelper;
import dawn.lib.Workspace;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DDBHelperImpl implements DDBHelper {

	static final byte ID_BOOLEAN = 1;
	static final byte ID_BYTE = 2;
	static final byte ID_SHORT = 3;
	static final byte ID_INT = 4;
	static final byte ID_LONG = 5;
	static final byte ID_FLOAT = 6;
	static final byte ID_DOUBLE = 7;
	static final byte ID_CHAR = 8;
	static final byte ID_STRING = 9;
	static final byte ID_TABLE = 10;
	static final byte ID_ARRAY = 11;
	static final byte ID_CUSTOM = 12;
	static final byte ID_HEADER = Byte.MIN_VALUE;
	private static final Map<Identifier, DDBDeserializer<?>> DESERIALIZERS = new HashMap<>();
	private final Workspace workspace;

	@Override
	public DawnDB newDB() {
		return new DDBImpl();
	}

	@Override
	public DawnDB loadDB(final String filename) throws IOException {
		final File f = this.workspace.getDataFile(filename);
		if (!f.exists()) {
			return this.newDB();
		}
		try (DataInputStream input = new DataInputStream(new FileInputStream(f))) {
			final byte[] header = new byte[2];
			input.readFully(header);
			if (header[0] != DDBHelperImpl.ID_HEADER || header[1] != DDBHelperImpl.ID_TABLE) {
				throw new IOException("Not a DawnDB file");
			}
			final DDBImpl result = new DDBImpl();
			result.load(input);
			return result;
		}
	}

	@Override
	public void storeDB(final DawnDB db, final String filename) throws IOException {
		final File f = this.workspace.getDataFile(filename);
		try (DataOutputStream output = new DataOutputStream(new FileOutputStream(f))) {
			output.writeByte(DDBHelperImpl.ID_HEADER);
			DDBHelperImpl.serialize(output, db);
		}
	}

	@Override
	public void addDeserializer(final Identifier identifier, final DDBDeserializer<?> deserializer) {
		DDBHelperImpl.DESERIALIZERS.put(identifier, deserializer);
	}

	static void serialize(final DataOutput output, final DDBValue value) throws IOException {
		final byte id = DDBHelperImpl.getId(value);
		output.writeByte(id);
		if (id != DDBHelperImpl.ID_CUSTOM || value instanceof DummyValueItem) {
			value.serialize(output);
		} else {
			DDBHelperImpl.writeString(output, value.getId().toString());
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (DataOutputStream dataOut = new DataOutputStream(baos)) {
				value.serialize(dataOut);
			}
			output.writeInt(baos.size());
			output.write(baos.toByteArray());
		}
	}

	static DDBValue deserialize(final DataInput input) throws IOException {
		final byte id = input.readByte();
		return switch (id) {
			case DDBHelperImpl.ID_BOOLEAN -> ValuePrimitive.make(input.readBoolean());
			case DDBHelperImpl.ID_BYTE -> ValuePrimitive.make(input.readByte());
			case DDBHelperImpl.ID_SHORT -> ValuePrimitive.make(input.readShort());
			case DDBHelperImpl.ID_INT -> ValuePrimitive.make(input.readInt());
			case DDBHelperImpl.ID_LONG -> ValuePrimitive.make(input.readLong());
			case DDBHelperImpl.ID_FLOAT -> ValuePrimitive.make(input.readFloat());
			case DDBHelperImpl.ID_DOUBLE -> ValuePrimitive.make(input.readDouble());
			case DDBHelperImpl.ID_CHAR -> ValuePrimitive.make(input.readChar());
			case DDBHelperImpl.ID_STRING -> ValuePrimitive.make(DDBHelperImpl.readString(input));
			case DDBHelperImpl.ID_TABLE -> DDBTableImpl.deserialize(input);
			case DDBHelperImpl.ID_ARRAY -> DDBArrayImpl.deserialize(input);
			case DDBHelperImpl.ID_CUSTOM -> DDBHelperImpl.deserializeCustom(input);
			default -> throw new IOException("Encountered unexpected value");
		};
	}

	static void writeString(final DataOutput output, final String str) throws IOException {
		final byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		output.writeInt(bytes.length);
		output.write(bytes);
	}

	static String readString(final DataInput input) throws IOException {
		final byte[] bytes = new byte[input.readInt()];
		input.readFully(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	private static DDBValue deserializeCustom(final DataInput input) throws IOException {
		final Identifier id = Identifier.of(DDBHelperImpl.readString(input));
		final byte[] data = new byte[input.readInt()];
		input.readFully(data);
		final DDBDeserializer<?> deserializer = DDBHelperImpl.DESERIALIZERS.get(id);
		if (deserializer == null) {
			return new DummyValueItem(id, data);
		}
		try (DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data))) {
			return deserializer.deserialize(dataIn);
		}
	}

	private static byte getId(final DDBValue value) {
		return switch (value) {
			case final ValuePrimitive.Boolean _ -> DDBHelperImpl.ID_BOOLEAN;
			case final ValuePrimitive.Byte _ -> DDBHelperImpl.ID_BYTE;
			case final ValuePrimitive.Short _ -> DDBHelperImpl.ID_SHORT;
			case final ValuePrimitive.Int _ -> DDBHelperImpl.ID_INT;
			case final ValuePrimitive.Long _ -> DDBHelperImpl.ID_LONG;
			case final ValuePrimitive.Float _ -> DDBHelperImpl.ID_FLOAT;
			case final ValuePrimitive.Double _ -> DDBHelperImpl.ID_DOUBLE;
			case final ValuePrimitive.Char _ -> DDBHelperImpl.ID_CHAR;
			case final ValuePrimitive.String _ -> DDBHelperImpl.ID_STRING;
			case final DDBTable _ -> DDBHelperImpl.ID_TABLE;
			case final DDBArray _ -> DDBHelperImpl.ID_ARRAY;
			default -> DDBHelperImpl.ID_CUSTOM;
		};
	}
}
