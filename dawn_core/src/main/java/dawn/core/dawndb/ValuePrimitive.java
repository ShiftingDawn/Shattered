package dawn.core.dawndb;

import java.io.DataOutput;
import java.io.IOException;
import dawn.Identifier;
import dawn.dawndb.DDBValue;

sealed abstract class ValuePrimitive<T> implements DDBValue
	permits ValuePrimitive.Boolean, ValuePrimitive.Byte, ValuePrimitive.Char, ValuePrimitive.Double, ValuePrimitive.Float, ValuePrimitive.Int, ValuePrimitive.Long, ValuePrimitive.Short, ValuePrimitive.String {

	private final Identifier id;
	private final T value;
	private final IOFunction<DataOutput, T> writeFunction;

	public ValuePrimitive(final java.lang.String id, final T value, final IOFunction<DataOutput, T> writeFunction) {
		this.id = Identifier.of(id);
		this.value = value;
		this.writeFunction = writeFunction;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public T get() {
		return this.value;
	}

	@Override
	public void serialize(final DataOutput output) throws IOException {
		this.writeFunction.apply(output, this.get());
	}

	@SuppressWarnings("unchecked")
	public static <T> ValuePrimitive<T> make(final T value) {
		return switch (value) {
			case final java.lang.Boolean data -> (ValuePrimitive<T>) new Boolean(data);
			case final java.lang.Byte data -> (ValuePrimitive<T>) new Byte(data);
			case final java.lang.Short data -> (ValuePrimitive<T>) new Short(data);
			case final Integer data -> (ValuePrimitive<T>) new Int(data);
			case final java.lang.Long data -> (ValuePrimitive<T>) new Long(data);
			case final java.lang.Float data -> (ValuePrimitive<T>) new Float(data);
			case final java.lang.Double data -> (ValuePrimitive<T>) new Double(data);
			case final Character data -> (ValuePrimitive<T>) new Char(data);
			case final java.lang.String data -> (ValuePrimitive<T>) new String(data);
			case null, default -> throw new IllegalArgumentException("Invalid value: " + value);
		};
	}

	public static final class Boolean extends ValuePrimitive<java.lang.Boolean> {

		private Boolean(final java.lang.Boolean value) {
			super("boolean", value, DataOutput::writeBoolean);
		}

	}

	public static final class Byte extends ValuePrimitive<java.lang.Byte> {

		private Byte(final java.lang.Byte value) {
			super("byte", value, DataOutput::writeByte);
		}
	}

	public static final class Short extends ValuePrimitive<java.lang.Short> {

		private Short(final java.lang.Short value) {
			super("short", value, DataOutput::writeShort);
		}
	}

	public static final class Int extends ValuePrimitive<java.lang.Integer> {

		private Int(final java.lang.Integer value) {
			super("int", value, DataOutput::writeInt);
		}
	}

	public static final class Long extends ValuePrimitive<java.lang.Long> {

		private Long(final java.lang.Long value) {
			super("long", value, DataOutput::writeLong);
		}
	}

	public static final class Float extends ValuePrimitive<java.lang.Float> {

		private Float(final java.lang.Float value) {
			super("float", value, DataOutput::writeFloat);
		}
	}

	public static final class Double extends ValuePrimitive<java.lang.Double> {

		private Double(final java.lang.Double value) {
			super("double", value, DataOutput::writeDouble);
		}
	}

	public static final class Char extends ValuePrimitive<java.lang.Character> {

		private Char(final java.lang.Character value) {
			super("char", value, DataOutput::writeChar);
		}
	}

	public static final class String extends ValuePrimitive<java.lang.String> {

		private String(final java.lang.String value) {
			super("string", value, DDBHelperImpl::writeString);
		}
	}
}
