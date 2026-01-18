package dawn.dawndb;

import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface DDBTable extends DDBValue {

	void set(String key, boolean data);

	void set(String key, byte data);

	void set(String key, short data);

	void set(String key, int data);

	void set(String key, long data);

	void set(String key, float data);

	void set(String key, double data);

	void set(String key, char data);

	void set(String key, String data);

	void set(String key, DDBTable data);

	void set(String key, DDBArray data);

	DDBTable setTable(String key);

	DDBArray setArray(String key);

	boolean has(String key);

	boolean hasBoolean(String key);

	boolean hasByte(String key);

	boolean hasShort(String key);

	boolean hasInt(String key);

	boolean hasLong(String key);

	boolean hasFloat(String key);

	boolean hasDouble(String key);

	boolean hasChar(String key);

	boolean hasString(String key);

	boolean hasTable(String key);

	boolean hasArray(String key);

	default boolean getBoolean(final String key) {
		return this.getBoolean(key, false);
	}

	default byte getByte(final String key) {
		return this.getByte(key, (byte) 0);
	}

	default short getShort(final String key) {
		return this.getShort(key, (short) 0);
	}

	default int getInt(final String key) {
		return this.getInt(key, 0);
	}

	default long getLong(final String key) {
		return this.getLong(key, 0L);
	}

	default float getFloat(final String key) {
		return this.getFloat(key, 0F);
	}

	default double getDouble(final String key) {
		return this.getDouble(key, 0.0);
	}

	default char getChar(final String key) {
		return this.getChar(key, (char) 0);
	}

	default String getString(final String key) {
		return this.getString(key, "");
	}

	DDBTable getTable(String key);

	DDBArray getArray(String key);

	boolean getBoolean(String key, boolean fallback);

	byte getByte(String key, byte fallback);

	short getShort(String key, short fallback);

	int getInt(String key, int fallback);

	long getLong(String key, long fallback);

	float getFloat(String key, float fallback);

	double getDouble(String key, double fallback);

	char getChar(String key, char fallback);

	String getString(String key, String fallback);

	DDBTable getTable(String key, DDBTable fallback);

	DDBArray getArray(String key, DDBArray fallback);

	boolean getBoolean(String key, BooleanSupplier fallback);

	byte getByte(String key, ByteSupplier fallback);

	short getShort(String key, ShortSupplier fallback);

	int getInt(String key, IntSupplier fallback);

	long getLong(String key, LongSupplier fallback);

	float getFloat(String key, FloatSupplier fallback);

	double getDouble(String key, DoubleSupplier fallback);

	char getChar(String key, CharSupplier fallback);

	String getString(String key, Supplier<String> fallback);

	DDBTable getTable(String key, Supplier<DDBTable> fallback);

	DDBArray getArray(String key, Supplier<DDBArray> fallback);

	Set<String> getKeySet();

	int size();
}
