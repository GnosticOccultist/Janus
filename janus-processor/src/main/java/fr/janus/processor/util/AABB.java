package fr.janus.processor.util;

public class AABB {

	private final Vector3f min;

	private final Vector3f max;

	public AABB(Vector3f min, Vector3f max) {
		this.min = min;
		this.max = max;
	}

	public Vector3f min() {
		return min;
	}

	public Vector3f max() {
		return max;
	}
	
	@Override
	public String toString() {
		return "AABB[min= " + min + ", max= " + max + "]";
	}
}
