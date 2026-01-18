package dawn.core.dawndb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import dawn.Dawn;
import dawn.Identifier;
import dawn.dawndb.ByteSupplier;
import dawn.dawndb.CharSupplier;
import dawn.dawndb.DDBArray;
import dawn.dawndb.DDBTable;
import dawn.dawndb.DDBValue;
import dawn.dawndb.FloatSupplier;
import dawn.dawndb.ShortSupplier;

sealed class DDBTableImpl implements DDBTable permits DDBImpl {

	private static final Identifier ID = Identifier.of("table");
	private final Map<String, DDBValue> values = new HashMap<>();

	@Override
	public Identifier getId() {
		return DDBTableImpl.ID;
	}

	@Override
	public void set(final String key, final boolean data) {
		this.values.put(key, ValuePrimitive.make(data));
	}

	@Override
	public void set(final String key, final byte data) {
		this.values.put(key, ValuePrimitive.make(data));
	}

	@Override
	public void set(final String key, final short data) {
		this.values.put(key, ValuePrimitive.make(data));
	}

	@Override
	public void set(final String key, final int data) {
		this.values.put(key, ValuePrimitive.make(data));
	}

	@Override
	public void set(final String key, final long data) {
		this.values.put(key, ValuePrimitive.make(data));
	}

	@Override
	public void set(final String key, final float data) {
		this.values.put(key, ValuePrimitive.make(data));
	}

	@Override
	public void set(final String key, final double data) {
		this.values.put(key, ValuePrimitive.make(data));
	}

	@Override
	public void set(final String key, final char data) {
		this.values.put(key, ValuePrimitive.make(data));
	}

	@Override
	public void set(final String key, final String data) {
		this.values.put(key, ValuePrimitive.make(data));
	}

	@Override
	public void set(final String key, final DDBTable data) {
		this.values.put(key, data);
	}

	@Override
	public void set(final String key, final DDBArray data) {
		this.values.put(key, data);
	}

	@Override
	public DDBTable setTable(final String key) {
		return Dawn.make(new DDBTableImpl(), table -> this.set(key, table));
	}

	@Override
	public DDBArray setArray(final String key) {
		return Dawn.make(new DDBArrayImpl(), array -> this.set(key, array));
	}

	@Override
	public boolean has(final String key) {
		return this.values.containsKey(key);
	}

	@Override
	public boolean hasBoolean(final String key) {
		return this.values.get(key) instanceof ValuePrimitive.Boolean;
	}

	@Override
	public boolean hasByte(final String key) {
		return this.values.get(key) instanceof ValuePrimitive.Byte;
	}

	@Override
	public boolean hasShort(final String key) {
		return this.values.get(key) instanceof ValuePrimitive.Short;
	}

	@Override
	public boolean hasInt(final String key) {
		return this.values.get(key) instanceof ValuePrimitive.Int;
	}

	@Override
	public boolean hasLong(final String key) {
		return this.values.get(key) instanceof ValuePrimitive.Long;
	}

	@Override
	public boolean hasFloat(final String key) {
		return this.values.get(key) instanceof ValuePrimitive.Float;
	}

	@Override
	public boolean hasDouble(final String key) {
		return this.values.get(key) instanceof ValuePrimitive.Double;
	}

	@Override
	public boolean hasChar(final String key) {
		return this.values.get(key) instanceof ValuePrimitive.Char;
	}

	@Override
	public boolean hasString(final String key) {
		return this.values.get(key) instanceof ValuePrimitive.String;
	}

	@Override
	public boolean hasTable(final String key) {
		return this.values.get(key) instanceof DDBTable;
	}

	@Override
	public boolean hasArray(final String key) {
		return this.values.get(key) instanceof DDBArray;
	}

	@Override
	public DDBTable getTable(final String key) {
		return this.getTable(key, DDBTableImpl::new);
	}

	@Override
	public DDBArray getArray(final String key) {
		return this.getArray(key, DDBArrayImpl::new);
	}

	@Override
	public boolean getBoolean(final String key, final boolean fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Boolean val ? val.get() : fallback;
	}

	@Override
	public byte getByte(final String key, final byte fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Byte val ? val.get() : fallback;
	}

	@Override
	public short getShort(final String key, final short fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Short val ? val.get() : fallback;
	}

	@Override
	public int getInt(final String key, final int fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Int val ? val.get() : fallback;
	}

	@Override
	public long getLong(final String key, final long fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Long val ? val.get() : fallback;
	}

	@Override
	public float getFloat(final String key, final float fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Float val ? val.get() : fallback;
	}

	@Override
	public double getDouble(final String key, final double fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Double val ? val.get() : fallback;
	}

	@Override
	public char getChar(final String key, final char fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Char val ? val.get() : fallback;
	}

	@Override
	public String getString(final String key, final String fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.String val ? val.get() : fallback;
	}

	@Override
	public DDBTable getTable(final String key, final DDBTable fallback) {
		return this.values.get(key) instanceof final DDBTable val ? val : fallback;
	}

	@Override
	public DDBArray getArray(final String key, final DDBArray fallback) {
		return this.values.get(key) instanceof final DDBArray val ? val : fallback;
	}

	@Override
	public boolean getBoolean(final String key, final BooleanSupplier fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Boolean val ? val.get() : fallback.getAsBoolean();
	}

	@Override
	public byte getByte(final String key, final ByteSupplier fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Byte val ? val.get() : fallback.getAsByte();
	}

	@Override
	public short getShort(final String key, final ShortSupplier fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Short val ? val.get() : fallback.getAsShort();
	}

	@Override
	public int getInt(final String key, final IntSupplier fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Int val ? val.get() : fallback.getAsInt();
	}

	@Override
	public long getLong(final String key, final LongSupplier fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Long val ? val.get() : fallback.getAsLong();
	}

	@Override
	public float getFloat(final String key, final FloatSupplier fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Float val ? val.get() : fallback.getAsFloat();
	}

	@Override
	public double getDouble(final String key, final DoubleSupplier fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Double val ? val.get() : fallback.getAsDouble();
	}

	@Override
	public char getChar(final String key, final CharSupplier fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.Char val ? val.get() : fallback.getAsChar();
	}

	@Override
	public String getString(final String key, final Supplier<String> fallback) {
		return this.values.get(key) instanceof final ValuePrimitive.String val ? val.get() : fallback.get();
	}

	@Override
	public DDBTable getTable(final String key, final Supplier<DDBTable> fallback) {
		return this.values.get(key) instanceof final DDBTable val ? val : fallback.get();
	}

	@Override
	public DDBArray getArray(final String key, final Supplier<DDBArray> fallback) {
		return this.values.get(key) instanceof final DDBArray val ? val : fallback.get();
	}

	@Override
	public Set<String> getKeySet() {
		return this.values.keySet();
	}

	@Override
	public int size() {
		return this.values.size();
	}

	@Override
	public void serialize(final DataOutput output) throws IOException {
		final Set<String> keys = this.values.keySet();
		output.writeInt(keys.size());
		for (final String key : keys) {
			DDBHelperImpl.writeString(output, key);
			DDBHelperImpl.serialize(output, this.values.get(key));
		}
	}

	static void deserialize(final DataInput input, final DDBTableImpl dest) throws IOException {
		final int keyCount = input.readInt();
		for (int i = 0; i < keyCount; ++i) {
			final String key = DDBHelperImpl.readString(input);
			final DDBValue value = DDBHelperImpl.deserialize(input);
			dest.values.put(key, value);
		}
	}

	static DDBTable deserialize(final DataInput input) throws IOException {
		final DDBTableImpl table = new DDBTableImpl();
		DDBTableImpl.deserialize(input, table);
		return table;
	}
}
