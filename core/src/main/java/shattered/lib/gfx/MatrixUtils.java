package shattered.lib.gfx;

import org.joml.Matrix4f;

public final class MatrixUtils {

	public static Matrix4f ortho() {
		return new Matrix4f().ortho(0, Display.get().getWidth(), Display.get().getHeight(), 0, 1, -1);
	}

	private MatrixUtils() {
	}
}
