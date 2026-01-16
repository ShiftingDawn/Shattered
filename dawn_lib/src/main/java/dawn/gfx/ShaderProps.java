package dawn.gfx;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import dawn.asset.Shader;
import dawn.internal.DawnLib;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public interface ShaderProps {

	static ShaderProps get() {
		return DawnLib.SHADER_PROPS;
	}

	int getNamedLocation(Shader shader, String name);

	void setUniform1(int location, float data);

	void setUniform1(int location, float[] data);

	void setUniform1(int location, FloatBuffer data);

	void setUniform1(int location, int data);

	void setUniform1(int location, int[] data);

	void setUniform1(int location, IntBuffer data);

	void setUniformUnsigned1(int location, int data);

	void setUniformUnsigned1(int location, int[] data);

	void setUniformUnsigned1(int location, IntBuffer data);

	void setUniform2(int location, float d1, float d2);

	void setUniform2(int location, float[] data);

	void setUniform2(int location, FloatBuffer data);

	void setUniform2(int location, int d1, int d2);

	void setUniform2(int location, int[] data);

	void setUniform2(int location, IntBuffer data);

	void setUniform2(int location, boolean transpose, Matrix2f mat);

	void setUniformUnsigned2(int location, int d1, int d2);

	void setUniformUnsigned2(int location, int[] data);

	void setUniformUnsigned2(int location, IntBuffer data);

	void setUniform3(int location, float d1, float d2, float d3);

	void setUniform3(int location, float[] data);

	void setUniform3(int location, FloatBuffer data);

	void setUniform3(int location, int d1, int d2, int d3);

	void setUniform3(int location, int[] data);

	void setUniform3(int location, IntBuffer data);

	void setUniform3(int location, boolean transpose, Matrix3f mat);

	void setUniformUnsigned3(int location, int d1, int d2, int d3);

	void setUniformUnsigned3(int location, int[] data);

	void setUniformUnsigned3(int location, IntBuffer data);

	void setUniform4(int location, float d1, float d2, float d3, float d4);

	void setUniform4(int location, float[] data);

	void setUniform4(int location, FloatBuffer data);

	void setUniform4(int location, int d1, int d2, int d3, int d4);

	void setUniform4(int location, int[] data);

	void setUniform4(int location, IntBuffer data);

	void setUniform4(int location, boolean transpose, Matrix4f mat);

	void setUniformUnsigned4(int location, int d1, int d2, int d3, int d4);

	void setUniformUnsigned4(int location, int[] data);

	void setUniformUnsigned4(int location, IntBuffer data);
}
