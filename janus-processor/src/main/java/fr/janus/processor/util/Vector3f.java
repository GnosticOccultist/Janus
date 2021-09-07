package fr.janus.processor.util;

import java.util.Arrays;

public class Vector3f implements Comparable<Vector3f> {

	private final float[] vector;

	public Vector3f() {
		this(0, 0, 0);
	}

	public Vector3f(float[] values, int offset) {
		this(values[offset + 0], values[offset + 1], values[offset + 2]);
	}

	public Vector3f(Vector3f other) {
		this(other.vector[0], other.vector[1], other.vector[2]);
	}

	public Vector3f(float x, float y, float z) {
		this.vector = new float[3];
		set(x, y, z);
	}

	public Vector3f set(float x, float y, float z) {
		this.vector[0] = x;
		this.vector[1] = y;
		this.vector[2] = z;
		return this;
	}

	public void min(Vector3f other) {
		min(other.vector, 0);
	}

	public void min(float[] vertices, int i) {
		this.vector[0] = Math.min(vector[0], vertices[i + 0]);
		this.vector[1] = Math.min(vector[1], vertices[i + 1]);
		this.vector[2] = Math.min(vector[2], vertices[i + 2]);
	}

	public void max(Vector3f other) {
		max(other.vector, 0);
	}

	public void max(float[] vertices, int i) {
		this.vector[0] = Math.max(vector[0], vertices[i + 0]);
		this.vector[1] = Math.max(vector[1], vertices[i + 1]);
		this.vector[2] = Math.max(vector[2], vertices[i + 2]);
	}
	
	public Vector3f add(Vector3f amount, Vector3f store) {
		store.set(x() + amount.x(), y() + amount.y(), z() + amount.z());
		return store;
	}
	
	public Vector3f sub(Vector3f amount, Vector3f store) {
		store.set(x() - amount.x(), y() - amount.y(), z() - amount.z());
		return store;
	}
	
	public void div(float divisor) {
		set(x() / divisor, y() / divisor, z() / divisor);
	}
	
	public float x() {
		return vector[0];
	}
	
	public float y() {
		return vector[1];
	}
	
	public float z() {
		return vector[2];
	}

	@Override
	public int compareTo(Vector3f other) {
		int r = Float.compare(vector[0], other.vector[0]);
		if (r != 0) {
			return r;
		}
		r = Float.compare(vector[1], other.vector[1]);
		if (r != 0) {
			return r;
		}

		return Float.compare(vector[2], other.vector[2]);
	}

	@Override
	public String toString() {
		return Arrays.toString(vector);
	}
}
