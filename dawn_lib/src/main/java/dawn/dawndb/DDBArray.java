package dawn.dawndb;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface DDBArray extends DDBValue {

	void add(boolean data);

	void add(byte data);

	void add(short data);

	void add(int data);

	void add(long data);

	void add(float data);

	void add(double data);

	void add(char data);

	void add(String data);

	void add(DDBTable data);

	void add(DDBArray data);

	DDBTable addTable();

	DDBArray addArray();

	void set(int index, boolean data);

	void set(int index, byte data);

	void set(int index, short data);

	void set(int index, int data);

	void set(int index, long data);

	void set(int index, float data);

	void set(int index, double data);

	void set(int index, char data);

	void set(int index, String data);

	void set(int index, DDBTable data);

	void set(int index, DDBArray data);

	DDBTable setTable(int index);

	DDBArray setArray(int index);

	boolean has(int index);

	boolean hasBoolean(int index);

	boolean hasByte(int index);

	boolean hasShort(int index);

	boolean hasInt(int index);

	boolean hasLong(int index);

	boolean hasFloat(int index);

	boolean hasDouble(int index);

	boolean hasChar(int index);

	boolean hasString(int index);

	boolean hasTable(int index);

	boolean hasArray(int index);

	default boolean getBoolean(final int index) {
		return this.getBoolean(index, false);
	}

	default byte getByte(final int index) {
		return this.getByte(index, (byte) 0);
	}

	default short getShort(final int index) {
		return this.getShort(index, (short) 0);
	}

	default int getInt(final int index) {
		return this.getInt(index, 0);
	}

	default long getLong(final int index) {
		return this.getLong(index, 0L);
	}

	default float getFloat(final int index) {
		return this.getFloat(index, 0F);
	}

	default double getDouble(final int index) {
		return this.getDouble(index, 0.0);
	}

	default char getChar(final int index) {
		return this.getChar(index, (char) 0);
	}

	default String getString(final int index) {
		return this.getString(index, "");
	}

	DDBTable getTable(int index);

	DDBArray getArray(int index);

	boolean getBoolean(int index, boolean fallback);

	byte getByte(int index, byte fallback);

	short getShort(int index, short fallback);

	int getInt(int index, int fallback);

	long getLong(int index, long fallback);

	float getFloat(int index, float fallback);

	double getDouble(int index, double fallback);

	char getChar(int index, char fallback);

	String getString(int index, String fallback);

	DDBTable getTable(int index, DDBTable fallback);

	DDBArray getArray(int index, DDBArray fallback);

	boolean getBoolean(int index, BooleanSupplier fallback);

	byte getByte(int index, ByteSupplier fallback);

	short getShort(int index, ShortSupplier fallback);

	int getInt(int index, IntSupplier fallback);

	long getLong(int index, LongSupplier fallback);

	float getFloat(int index, FloatSupplier fallback);

	double getDouble(int index, DoubleSupplier fallback);

	char getChar(int index, CharSupplier fallback);

	String getString(int index, Supplier<String> fallback);

	DDBTable getTable(int index, Supplier<DDBTable> fallback);

	DDBArray getArray(int index, Supplier<DDBArray> fallback);
	
	int size();
}
