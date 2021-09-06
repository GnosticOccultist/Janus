package fr.janus.processor;

import java.util.Arrays;

import fr.janus.processor.util.AABB;
import fr.janus.processor.util.Triangle;
import fr.janus.processor.util.Vector3f;

public class Model {

	private final float[] vertices;

	private final int[] indices;

	private final AABB bounds;

	public Model(float[] vertices, int vertexCount, int[] indices, int triangleCount) {
		this.vertices = new float[vertexCount * 3];
		this.indices = new int[triangleCount * 3];

		System.arraycopy(vertices, 0, this.vertices, 0, vertexCount * 3);
		System.arraycopy(indices, 0, this.indices, 0, triangleCount * 3);

		this.bounds = calculateAABB();
	}

	private AABB calculateAABB() {
		Vector3f max = new Vector3f(vertices, 0);
		Vector3f min = new Vector3f(vertices, 0);

		for (int i = 1; i < vertexCount(); ++i) {
			min.min(vertices, i * 3);
			max.max(vertices, i * 3);
		}
		
		return new AABB(min, max);
	}

	public int vertexCount() {
		return vertices.length / 3;
	}

	public int triangleCount() {
		return indices.length / 3;
	}

	public AABB getAABB() {
		return bounds;
	}

	public Triangle getTriangle(int index) {
		assert index < triangleCount();

		int idx1 = indices[index * 3];
		int idx2 = indices[index * 3 + 1];
		int idx3 = indices[index * 3 + 2];

		Vector3f a = new Vector3f(vertices[idx1 * 3], vertices[idx1 * 3 + 1], vertices[idx1 * 3 + 2]);
		Vector3f b = new Vector3f(vertices[idx2 * 3], vertices[idx2 * 3 + 1], vertices[idx2 * 3 + 2]);
		Vector3f c = new Vector3f(vertices[idx3 * 3], vertices[idx3 * 3 + 1], vertices[idx3 * 3 + 2]);

		return new Triangle(a, b, c);
	}

	@Override
	public String toString() {
		return "Model[vertices= " + vertexCount() + ", triangles= " + triangleCount() + "] " + Arrays.toString(vertices)
				+ " " + Arrays.toString(indices);
	}
}
