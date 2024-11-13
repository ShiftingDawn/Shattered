package shattered.lib.gfx;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import shattered.lib.util.Utils;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1fv;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform1iv;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform2fv;
import static org.lwjgl.opengl.GL20.glUniform2i;
import static org.lwjgl.opengl.GL20.glUniform2iv;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniform3i;
import static org.lwjgl.opengl.GL20.glUniform3iv;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniform4fv;
import static org.lwjgl.opengl.GL20.glUniform4i;
import static org.lwjgl.opengl.GL20.glUniform4iv;
import static org.lwjgl.opengl.GL20.glUniformMatrix2fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.glUniform1ui;
import static org.lwjgl.opengl.GL30.glUniform1uiv;
import static org.lwjgl.opengl.GL30.glUniform2ui;
import static org.lwjgl.opengl.GL30.glUniform2uiv;
import static org.lwjgl.opengl.GL30.glUniform3ui;
import static org.lwjgl.opengl.GL30.glUniform3uiv;
import static org.lwjgl.opengl.GL30.glUniform4ui;
import static org.lwjgl.opengl.GL30.glUniform4uiv;

public final class ShaderProps {

	public static int getNamedLocation(final ShaderProgram shader, final String name) {
		return glGetUniformLocation(shader.getProgram(), name);
	}

	public static void setUniform1(final int location, final float data) {
		glUniform1f(location, data);
	}

	public static void setUniform1(final int location, final float[] data) {
		glUniform1fv(location, data);
	}

	public static void setUniform1(final int location, final FloatBuffer data) {
		glUniform1fv(location, data);
	}

	public static void setUniform1(final int location, final int data) {
		glUniform1i(location, data);
	}

	public static void setUniform1(final int location, final int[] data) {
		glUniform1iv(location, data);
	}

	public static void setUniform1(final int location, final IntBuffer data) {
		glUniform1iv(location, data);
	}

	public static void setUniformUnsigned1(final int location, final int data) {
		glUniform1ui(location, data);
	}

	public static void setUniformUnsigned1(final int location, final int[] data) {
		glUniform1uiv(location, data);
	}

	public static void setUniformUnsigned1(final int location, final IntBuffer data) {
		glUniform1uiv(location, data);
	}

	public static void setUniform2(final int location, final float d1, final float d2) {
		glUniform2f(location, d1, d2);
	}

	public static void setUniform2(final int location, final float[] data) {
		glUniform2fv(location, data);
	}

	public static void setUniform2(final int location, final FloatBuffer data) {
		glUniform2fv(location, data);
	}

	public static void setUniform2(final int location, final int d1, final int d2) {
		glUniform2i(location, d1, d2);
	}

	public static void setUniform2(final int location, final int[] data) {
		glUniform2iv(location, data);
	}

	public static void setUniform2(final int location, final IntBuffer data) {
		glUniform2iv(location, data);
	}

	public static void setUniform2(final int location, final boolean transpose, final Matrix2f mat) {
		glUniformMatrix2fv(location, transpose, Utils.make(BufferUtils.createFloatBuffer(4), mat::get));
	}

	public static void setUniformUnsigned2(final int location, final int d1, final int d2) {
		glUniform2ui(location, d1, d2);
	}

	public static void setUniformUnsigned2(final int location, final int[] data) {
		glUniform2uiv(location, data);
	}

	public static void setUniformUnsigned2(final int location, final IntBuffer data) {
		glUniform2uiv(location, data);
	}

	public static void setUniform3(final int location, final float d1, final float d2, final float d3) {
		glUniform3f(location, d1, d2, d3);
	}

	public static void setUniform3(final int location, final float[] data) {
		glUniform3fv(location, data);
	}

	public static void setUniform3(final int location, final FloatBuffer data) {
		glUniform3fv(location, data);
	}

	public static void setUniform3(final int location, final int d1, final int d2, final int d3) {
		glUniform3i(location, d1, d2, d3);
	}

	public static void setUniform3(final int location, final int[] data) {
		glUniform3iv(location, data);
	}

	public static void setUniform3(final int location, final IntBuffer data) {
		glUniform3iv(location, data);
	}

	public static void setUniform2(final int location, final boolean transpose, final Matrix3f mat) {
		glUniformMatrix3fv(location, transpose, Utils.make(BufferUtils.createFloatBuffer(9), mat::get));
	}

	public static void setUniformUnsigned3(final int location, final int d1, final int d2, final int d3) {
		glUniform3ui(location, d1, d2, d3);
	}

	public static void setUniformUnsigned3(final int location, final int[] data) {
		glUniform3uiv(location, data);
	}

	public static void setUniformUnsigned3(final int location, final IntBuffer data) {
		glUniform3uiv(location, data);
	}

	public static void setUniform4(final int location, final float d1, final float d2, final float d3, final float d4) {
		glUniform4f(location, d1, d2, d3, d4);
	}

	public static void setUniform4(final int location, final float[] data) {
		glUniform4fv(location, data);
	}

	public static void setUniform4(final int location, final FloatBuffer data) {
		glUniform4fv(location, data);
	}

	public static void setUniform4(final int location, final int d1, final int d2, final int d3, final int d4) {
		glUniform4i(location, d1, d2, d3, d4);
	}

	public static void setUniform4(final int location, final int[] data) {
		glUniform4iv(location, data);
	}

	public static void setUniform4(final int location, final IntBuffer data) {
		glUniform4iv(location, data);
	}

	public static void setUniform4(final int location, final boolean transpose, final Matrix4f mat) {
		glUniformMatrix4fv(location, transpose, Utils.make(BufferUtils.createFloatBuffer(16), mat::get));
	}

	public static void setUniformUnsigned4(final int location, final int d1, final int d2, final int d3, final int d4) {
		glUniform4ui(location, d1, d2, d3, d4);
	}

	public static void setUniformUnsigned4(final int location, final int[] data) {
		glUniform4uiv(location, data);
	}

	public static void setUniformUnsigned4(final int location, final IntBuffer data) {
		glUniform4uiv(location, data);
	}

	private ShaderProps() {
	}
}
