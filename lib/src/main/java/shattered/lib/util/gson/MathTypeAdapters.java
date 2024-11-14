package shattered.lib.util.gson;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import shattered.lib.util.math.Dimension;
import shattered.lib.util.math.Point;
import shattered.lib.util.math.Rectangle;

final class MathTypeAdapters {

	private MathTypeAdapters() {
	}

	public static void register(final GsonBuilder builder) {
		builder.registerTypeAdapter(Point.class, new PointTypeAdapter());
		builder.registerTypeAdapter(Dimension.class, new DimensionTypeAdapter());
		builder.registerTypeAdapter(Rectangle.class, new RectangleTypeAdapter());
	}

	private static class PointTypeAdapter extends TypeAdapter<Point> {

		private static final Pattern STRING_PATTERN = Pattern.compile("^(\\d+.?\\d*):(\\d+.?\\d*)$");

		@Override
		public void write(final JsonWriter writer, final Point value) throws IOException {
			writer.beginObject();
			writer.name("x").value(value.getDoubleX());
			writer.name("y").value(value.getDoubleY());
			writer.endObject();
		}

		@Override
		public Point read(final JsonReader reader) throws IOException {
			switch (reader.peek()) {
				case STRING: {
					final String data = reader.nextString();
					final Matcher matcher = PointTypeAdapter.STRING_PATTERN.matcher(data);
					if (!matcher.matches()) {
						throw new JsonSyntaxException("Invalid coordinate format! Correct format: <x>:<y>");
					}
					final String x = matcher.group(1);
					final String y = matcher.group(2);
					return Point.create(Double.parseDouble(x), Double.parseDouble(y));
				}
				case BEGIN_OBJECT: {
					reader.beginObject();
					Double x = null;
					Double y = null;
					while (reader.hasNext()) {
						final String name = reader.nextName();
						switch (name) {
							case "x":
								x = reader.nextDouble();
								break;
							case "y":
								y = reader.nextDouble();
								break;
							default:
								throw new JsonSyntaxException("Invalid coordinate object key: " + name + ", expected x or y");
						}
					}
					reader.endObject();
					if (x == null || y == null) {
						throw new JsonSyntaxException("Incomplete coordinate format! Missing: " + (x == null ? "x" : "y"));
					}
					return Point.create(x, y);
				}
				default:
					throw new JsonSyntaxException("Expected STRING/BEGIN_OBJECT, got " + reader.peek());
			}
		}
	}

	private static class DimensionTypeAdapter extends TypeAdapter<Dimension> {

		private static final Pattern STRING_PATTERN = Pattern.compile("^(\\d+.?\\d*)x(\\d+.?\\d*)$");

		@Override
		public void write(final JsonWriter writer, final Dimension value) throws IOException {
			writer.beginObject();
			writer.name("width").value(value.getDoubleWidth());
			writer.name("height").value(value.getDoubleHeight());
			writer.endObject();
		}

		@Override
		public Dimension read(final JsonReader reader) throws IOException {
			switch (reader.peek()) {
				case STRING: {
					final String data = reader.nextString();
					final Matcher matcher = DimensionTypeAdapter.STRING_PATTERN.matcher(data);
					if (!matcher.matches()) {
						throw new JsonSyntaxException("Invalid dimension format! Correct format: <width>x<height>");
					}
					final String width = matcher.group(1);
					final String height = matcher.group(2);
					return Dimension.create(Double.parseDouble(width), Double.parseDouble(height));
				}
				case BEGIN_OBJECT: {
					reader.beginObject();
					Double width = null;
					Double height = null;
					while (reader.hasNext()) {
						final String name = reader.nextName();
						switch (name) {
							case "width":
							case "w":
								width = reader.nextDouble();
								break;
							case "height":
							case "h":
								height = reader.nextDouble();
								break;
							default:
								throw new JsonSyntaxException("Invalid dimension object key: " + name + ", expected width, w, height or h");
						}
					}
					reader.endObject();
					if (width == null || height == null) {
						throw new JsonSyntaxException("Incomplete dimension format! Missing: " + (width == null ? "width" : "height"));
					}
					return Dimension.create(width, height);
				}
				default:
					throw new JsonSyntaxException("Expected STRING/BEGIN_OBJECT, got " + reader.peek());
			}
		}
	}

	private static class RectangleTypeAdapter extends TypeAdapter<Rectangle> {

		private static final Pattern STRING_PATTERN = Pattern.compile("^(\\d+.?\\d*)x(\\d+.?\\d*)x(\\d+.?\\d*)x(\\d+.?\\d*)$");

		@Override
		public void write(final JsonWriter writer, final Rectangle value) throws IOException {
			writer.beginObject();
			writer.name("x").value(value.getDoubleX());
			writer.name("y").value(value.getDoubleY());
			writer.name("width").value(value.getDoubleWidth());
			writer.name("height").value(value.getDoubleHeight());
			writer.endObject();
		}

		@Override
		public Rectangle read(final JsonReader reader) throws IOException {
			switch (reader.peek()) {
				case STRING: {
					final String data = reader.nextString();
					final Matcher matcher = RectangleTypeAdapter.STRING_PATTERN.matcher(data);
					if (!matcher.matches()) {
						throw new JsonSyntaxException("Invalid rectangle format! Correct format: <x>x<y>x<width>x<height>");
					}
					final String x = matcher.group(1);
					final String y = matcher.group(2);
					final String width = matcher.group(3);
					final String height = matcher.group(4);
					return Rectangle.create(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(width), Double.parseDouble(height));
				}
				case BEGIN_OBJECT: {
					reader.beginObject();
					Double x = null;
					Double y = null;
					Double width = null;
					Double height = null;
					while (reader.hasNext()) {
						final String name = reader.nextName();
						switch (name) {
							case "x":
								x = reader.nextDouble();
								break;
							case "y":
								y = reader.nextDouble();
								break;
							case "width":
							case "w":
								width = reader.nextDouble();
								break;
							case "height":
							case "h":
								height = reader.nextDouble();
								break;
							default:
								throw new JsonSyntaxException("Invalid rectangle object key: " + name + ", expected x, y, width, w, height or h");
						}
					}
					reader.endObject();
					if (x == null || y == null || width == null || height == null) {
						throw new JsonSyntaxException(
								"Incomplete rectangle format! Missing:" +
										(x == null ? " x" : "") +
										(y == null ? " y" : "") +
										(width == null ? " width" : "") +
										(height == null ? " height" : "")
						);
					}
					return Rectangle.create(x, y, width, height);
				}
				default:
					throw new JsonSyntaxException("Expected STRING/BEGIN_OBJECT, got " + reader.peek());
			}
		}
	}
}
