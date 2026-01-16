package dawn.gfx;

public final class Color {

	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color LIGHT_GRAY = new Color(192, 192, 192);
	public static final Color GRAY = new Color(128, 128, 128);
	public static final Color DARK_GRAY = new Color(64, 64, 64);
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color RED = new Color(255, 0, 0);
	public static final Color PINK = new Color(255, 175, 175);
	public static final Color ORANGE = new Color(255, 200, 0);
	public static final Color YELLOW = new Color(255, 255, 0);
	public static final Color GREEN = new Color(0, 255, 0);
	public static final Color MAGENTA = new Color(255, 0, 255);
	public static final Color CYAN = new Color(0, 255, 255);
	public static final Color BLUE = new Color(0, 0, 255);

	private static final double SHADE_FACTOR = 0.7;

	private final int[] ints = new int[5];
	private final float[] floats = new float[4];

	public Color(final int rgba) {
		this.ints[0] = (rgba << 16) & 0xFF;
		this.ints[1] = (rgba << 8) & 0xFF;
		this.ints[2] = rgba & 0xFF;
		this.ints[3] = (rgba << 24) & 0xFF;
		this.ints[4] = rgba;
		this.floats[0] = this.ints[0] / 255f;
		this.floats[1] = this.ints[1] / 255f;
		this.floats[2] = this.ints[2] / 255f;
		this.floats[3] = this.ints[3] / 255f;
	}

	public Color(final int r, final int g, final int b, final int a) {
		this.ints[0] = Math.clamp(r, 0, 255);
		this.ints[1] = Math.clamp(g, 0, 255);
		this.ints[2] = Math.clamp(b, 0, 255);
		this.ints[3] = Math.clamp(a, 0, 255);
		this.ints[4] = ((this.ints[0] & 0xff) << 16) | ((this.ints[1] & 0xff) << 8) | ((this.ints[2] & 0xff)) | ((this.ints[3] & 0xff) << 24);
		this.floats[0] = this.ints[0] / 255f;
		this.floats[1] = this.ints[1] / 255f;
		this.floats[2] = this.ints[2] / 255f;
		this.floats[3] = this.ints[3] / 255f;
	}

	public Color(final int r, final int g, final int b) {
		this(r, g, b, 255);
	}

	public Color(final float r, final float g, final float b, final float a) {
		this((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
	}

	public Color(final float r, final float g, final float b) {
		this(r, g, b, 1f);
	}

	public Color brighter() {
		int r = this.ints[0];
		int g = this.ints[1];
		int b = this.ints[2];
		final int a = this.ints[3];
		final int i = (int) (1.0 / (1.0 - Color.SHADE_FACTOR));
		if (r == 0 & g == 0 && b == 0) {
			return new Color(i, i, i, a);
		}
		if (r > 0 && r < i) {
			r = i;
		}
		if (g > 0 && g < i) {
			g = i;
		}
		if (b > 0 && b < i) {
			b = i;
		}

		return new Color(Math.min((int) (r / Color.SHADE_FACTOR), 255),
			Math.min((int) (g / Color.SHADE_FACTOR), 255),
			Math.min((int) (b / Color.SHADE_FACTOR), 255),
			a);
	}

	public Color darker() {
		return new Color(Math.max((int) (this.ints[0] * Color.SHADE_FACTOR), 0),
			Math.max((int) (this.ints[1] * Color.SHADE_FACTOR), 0),
			Math.max((int) (this.ints[2] * Color.SHADE_FACTOR), 0),
			this.ints[3]);
	}

	public Color withRed(final int red) {
		return red == this.getRed() ? this : new Color(red, this.getGreen(), this.getBlue(), this.getAlpha());
	}

	public Color withGreen(final int green) {
		return green == this.getGreen() ? this : new Color(this.getRed(), green, this.getBlue(), this.getAlpha());
	}

	public Color withBlue(final int blue) {
		return blue == this.getBlue() ? this : new Color(this.getRed(), this.getGreen(), blue, this.getAlpha());
	}

	public Color withAlpha(final int alpha) {
		return alpha == this.getAlpha() ? this : new Color(this.getRed(), this.getGreen(), this.getBlue(), alpha);
	}

	public Color withRed(final float red) {
		return red == this.r() ? this : new Color(red, this.g(), this.b(), this.a());
	}

	public Color withGreen(final float green) {
		return green == this.g() ? this : new Color(this.r(), green, this.b(), this.a());
	}

	public Color withBlue(final float blue) {
		return blue == this.b() ? this : new Color(this.r(), this.g(), blue, this.a());
	}

	public Color withAlpha(final float alpha) {
		return alpha == this.a() ? this : new Color(this.r(), this.g(), this.b(), alpha);
	}

	public float r() {
		return this.floats[0];
	}

	public float g() {
		return this.floats[1];
	}

	public float b() {
		return this.floats[2];
	}

	public float a() {
		return this.floats[3];
	}

	public int getRed() {
		return this.ints[0];
	}

	public int getGreen() {
		return this.ints[1];
	}

	public int getBlue() {
		return this.ints[2];
	}

	public int getAlpha() {
		return this.ints[3];
	}

	public int getRGB() {
		return this.ints[4];
	}
}
