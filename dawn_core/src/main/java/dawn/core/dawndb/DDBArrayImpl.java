package dawn.core.dawndb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

final class DDBArrayImpl implements DDBArray {

	private static final Identifier ID = Identifier.of("array");
	private final List<DDBValue> values = new ArrayList<>();

	@Override
	public Identifier getId() {
		return DDBArrayImpl.ID;
	}

	@Override
	public void add(final boolean data) {
		this.values.add(ValuePrimitive.make(data));
	}

	@Override
	public void add(final byte data) {
		this.values.add(ValuePrimitive.make(data));
	}

	@Override
	public void add(final short data) {
		this.values.add(ValuePrimitive.make(data));
	}

	@Override
	public void add(final int data) {
		this.values.add(ValuePrimitive.make(data));
	}

	@Override
	public void add(final long data) {
		this.values.add(ValuePrimitive.make(data));
	}

	@Override
	public void add(final float data) {
		this.values.add(ValuePrimitive.make(data));
	}

	@Override
	public void add(final double data) {
		this.values.add(ValuePrimitive.make(data));
	}

	@Override
	public void add(final char data) {
		this.values.add(ValuePrimitive.make(data));
	}

	@Override
	public void add(final String data) {
		this.values.add(ValuePrimitive.make(data));
	}

	@Override
	public void add(final DDBTable data) {
		this.values.add(data);
	}

	@Override
	public void add(final DDBArray data) {
		this.values.add(data);
	}

	@Override
	public DDBTable addTable() {
		return Dawn.make(new DDBTableImpl(), this::add);
	}

	@Override
	public DDBArray addArray() {
		return Dawn.make(new DDBArrayImpl(), this::add);
	}

	@Override
	public void set(final int index, final boolean data) {
		Objects.checkIndex(index, this.values.size());
		this.values.set(index, ValuePrimitive.make(data));
	}

	@Override
	public void set(final int index, final byte data) {
		Objects.checkIndex(index, this.values.size());
		this.values.set(index, ValuePrimitive.make(data));
	}

	@Override
	public void set(final int index, final short data) {
		Objects.checkIndex(index, this.values.size());
		this.values.set(index, ValuePrimitive.make(data));
	}

	@Override
	public void set(final int index, final int data) {
		Objects.checkIndex(index, this.values.size());
		this.values.set(index, ValuePrimitive.make(data));
	}

	@Override
	public void set(final int index, final long data) {
		Objects.checkIndex(index, this.values.size());
		this.values.set(index, ValuePrimitive.make(data));
	}

	@Override
	public void set(final int index, final float data) {
		Objects.checkIndex(index, this.values.size());
		this.values.set(index, ValuePrimitive.make(data));
	}

	@Override
	public void set(final int index, final double data) {
		Objects.checkIndex(index, this.values.size());
		this.values.set(index, ValuePrimitive.make(data));
	}

	@Override
	public void set(final int index, final char data) {
		Objects.checkIndex(index, this.values.size());
		this.values.set(index, ValuePrimitive.make(data));
	}

	@Override
	public void set(final int index, final String data) {
		Objects.checkIndex(index, this.values.size());
		this.values.set(index, ValuePrimitive.make(data));
	}

	@Override
	public void set(final int index, final DDBTable data) {
		Objects.checkIndex(index, this.values.size());
		this.values.set(index, data);
	}

	@Override
	public void set(final int index, final DDBArray data) {
		Objects.checkIndex(index, this.values.size());
		this.values.set(index, data);
	}

	@Override
	public DDBTable setTable(final int index) {
		Objects.checkIndex(index, this.values.size());
		return Dawn.make(new DDBTableImpl(), table -> this.set(index, table));
	}

	@Override
	public DDBArray setArray(final int index) {
		Objects.checkIndex(index, this.values.size());
		return Dawn.make(new DDBArrayImpl(), array -> this.set(index, array));
	}

	@Override
	public boolean has(final int index) {
		return index >= 0 && index < this.values.size();
	}

	@Override
	public boolean hasBoolean(final int index) {
		return this.has(index) && this.values.get(index) instanceof ValuePrimitive.Boolean;
	}

	@Override
	public boolean hasByte(final int index) {
		return this.has(index) && this.values.get(index) instanceof ValuePrimitive.Byte;
	}

	@Override
	public boolean hasShort(final int index) {
		return this.has(index) && this.values.get(index) instanceof ValuePrimitive.Short;
	}

	@Override
	public boolean hasInt(final int index) {
		return this.has(index) && this.values.get(index) instanceof ValuePrimitive.Int;
	}

	@Override
	public boolean hasLong(final int index) {
		return this.has(index) && this.values.get(index) instanceof ValuePrimitive.Long;
	}

	@Override
	public boolean hasFloat(final int index) {
		return this.has(index) && this.values.get(index) instanceof ValuePrimitive.Float;
	}

	@Override
	public boolean hasDouble(final int index) {
		return this.has(index) && this.values.get(index) instanceof ValuePrimitive.Double;
	}

	@Override
	public boolean hasChar(final int index) {
		return this.has(index) && this.values.get(index) instanceof ValuePrimitive.Char;
	}

	@Override
	public boolean hasString(final int index) {
		return this.has(index) && this.values.get(index) instanceof ValuePrimitive.String;
	}

	@Override
	public boolean hasTable(final int index) {
		return this.has(index) && this.values.get(index) instanceof DDBTable;
	}

	@Override
	public boolean hasArray(final int index) {
		return this.has(index) && this.values.get(index) instanceof DDBArray;
	}

	@Override
	public DDBTable getTable(final int index) {
		return this.getTable(index, DDBTableImpl::new);
	}

	@Override
	public DDBArray getArray(final int index) {
		return this.getArray(index, DDBArrayImpl::new);
	}

	@Override
	public boolean getBoolean(final int index, final boolean fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Boolean val ? val.get() : fallback;
	}

	@Override
	public byte getByte(final int index, final byte fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Byte val ? val.get() : fallback;
	}

	@Override
	public short getShort(final int index, final short fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Short val ? val.get() : fallback;
	}

	@Override
	public int getInt(final int index, final int fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Int val ? val.get() : fallback;
	}

	@Override
	public long getLong(final int index, final long fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Long val ? val.get() : fallback;
	}

	@Override
	public float getFloat(final int index, final float fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Float val ? val.get() : fallback;
	}

	@Override
	public double getDouble(final int index, final double fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Double val ? val.get() : fallback;
	}

	@Override
	public char getChar(final int index, final char fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Char val ? val.get() : fallback;
	}

	@Override
	public String getString(final int index, final String fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.String val ? val.get() : fallback;
	}

	@Override
	public DDBTable getTable(final int index, final DDBTable fallback) {
		return this.has(index) && this.values.get(index) instanceof final DDBTable val ? val : fallback;
	}

	@Override
	public DDBArray getArray(final int index, final DDBArray fallback) {
		return this.has(index) && this.values.get(index) instanceof final DDBArray val ? val : fallback;
	}

	@Override
	public boolean getBoolean(final int index, final BooleanSupplier fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Boolean val ? val.get() : fallback.getAsBoolean();
	}

	@Override
	public byte getByte(final int index, final ByteSupplier fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Byte val ? val.get() : fallback.getAsByte();
	}

	@Override
	public short getShort(final int index, final ShortSupplier fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Short val ? val.get() : fallback.getAsShort();
	}

	@Override
	public int getInt(final int index, final IntSupplier fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Int val ? val.get() : fallback.getAsInt();
	}

	@Override
	public long getLong(final int index, final LongSupplier fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Long val ? val.get() : fallback.getAsLong();
	}

	@Override
	public float getFloat(final int index, final FloatSupplier fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Float val ? val.get() : fallback.getAsFloat();
	}

	@Override
	public double getDouble(final int index, final DoubleSupplier fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Double val ? val.get() : fallback.getAsDouble();
	}

	@Override
	public char getChar(final int index, final CharSupplier fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.Char val ? val.get() : fallback.getAsChar();
	}

	@Override
	public String getString(final int index, final Supplier<String> fallback) {
		return this.has(index) && this.values.get(index) instanceof final ValuePrimitive.String val ? val.get() : fallback.get();
	}

	@Override
	public DDBTable getTable(final int index, final Supplier<DDBTable> fallback) {
		return this.has(index) && this.values.get(index) instanceof final DDBTable val ? val : fallback.get();
	}

	@Override
	public DDBArray getArray(final int index, final Supplier<DDBArray> fallback) {
		return this.has(index) && this.values.get(index) instanceof final DDBArray val ? val : fallback.get();
	}

	@Override
	public int size() {
		return this.values.size();
	}

	@Override
	public void serialize(final DataOutput output) throws IOException {
		output.writeInt(this.values.size());
		for (final DDBValue value : this.values) {
			DDBHelperImpl.serialize(output, value);
		}
	}

	static DDBArray deserialize(final DataInput input) throws IOException {
		final DDBArrayImpl array = new DDBArrayImpl();
		final int arraySize = input.readInt();
		for (int i = 0; i < arraySize; ++i) {
			final DDBValue value = DDBHelperImpl.deserialize(input);
			array.values.add(value);
		}
		return array;
	}
}
