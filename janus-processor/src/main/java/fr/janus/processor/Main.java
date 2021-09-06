package fr.janus.processor;

public class Main {

	public static void main(String[] args) {
		float[] vertices = new float[] { 0, 0, 0, 0, -1, 0, 1, 0, 0 };
		int[] indices = new int[] { 0, 1, 2 };

		// A single triangle.
		Model first = new Model(vertices, 3, indices, 1);
		
		vertices = new float[] { 0, -1, 0, 1, -1, 0, 1, 0, 0 };
		indices = new int[] { 1, 3, 2 };
		
		Model second = new Model(vertices, 3, indices, 1);
		
		Scene scene = new Scene();
		scene.addModel(first);
		scene.addModel(second);
		
		System.out.println(scene.getAABB());
	}
}
