package fr.janus.processor.util;

import java.util.Arrays;

public class Vector3i implements Comparable<Vector3i> {

	private final int[] vector;

	public Vector3i() {
		this(0, 0, 0);
	}

	public Vector3i(int[] values, int offset) {
		this(values[offset + 0], values[offset + 1], values[offset + 2]);
	}

	public Vector3i(Vector3i other) {
		this(other.vector[0], other.vector[1], other.vector[2]);
	}

	public Vector3i(int x, int y, int z) {
		this.vector = new int[3];
		set(x, y, z);
	}

	public Vector3i set(int x, int y, int z) {
		this.vector[0] = x;
		this.vector[1] = y;
		this.vector[2] = z;
		return this;
	}

	public int x() {
		return vector[0];
	}

	public int y() {
		return vector[1];
	}

	public int z() {
		return vector[2];
	}

	@Override
	public int compareTo(Vector3i other) {
		int r = Integer.compare(vector[0], other.vector[0]);
		if (r != 0) {
			return r;
		}
		r = Integer.compare(vector[1], other.vector[1]);
		if (r != 0) {
			return r;
		}

		return Integer.compare(vector[2], other.vector[2]);
	}

	@Override
	public String toString() {
		return Arrays.toString(vector);
	}

}
