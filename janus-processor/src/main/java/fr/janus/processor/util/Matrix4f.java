package fr.janus.processor.util;

public class Matrix4f {

	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;

	public Matrix4f() {
		identity();
	}

	public Matrix4f identity() {
		m01 = m02 = m03 = 0.0f;
		m10 = m12 = m13 = 0.0f;
		m20 = m21 = m23 = 0.0f;
		m30 = m31 = m32 = 0.0f;
		m00 = m11 = m22 = m33 = 1.0f;
		return this;
	}

	@Override
	public String toString() {
		var result = new StringBuffer(getClass().getSimpleName() + "\n[\n");
		result.append(' ');
		result.append(m00);
		result.append(' ');
		result.append(m01);
		result.append(' ');
		result.append(m02);
		result.append(' ');
		result.append(m03);
		result.append(" \n");

		result.append(' ');
		result.append(m10);
		result.append(' ');
		result.append(m11);
		result.append(' ');
		result.append(m12);
		result.append(' ');
		result.append(m13);
		result.append(" \n");

		result.append(' ');
		result.append(m20);
		result.append(' ');
		result.append(m21);
		result.append(' ');
		result.append(m22);
		result.append(' ');
		result.append(m23);
		result.append(" \n");

		result.append(' ');
		result.append(m30);
		result.append(' ');
		result.append(m31);
		result.append(' ');
		result.append(m32);
		result.append(' ');
		result.append(m33);
		result.append(" \n");

		result.append(']');
		return result.toString();
	}
}
