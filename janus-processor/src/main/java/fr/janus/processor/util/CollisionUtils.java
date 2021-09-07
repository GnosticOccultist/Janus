package fr.janus.processor.util;

public class CollisionUtils {

	private CollisionUtils() {
	}

	public static int triBoxOverlaps(float[] boxCenter, float[] boxHalfSize, float[][] triVertices) {
		/*
		 * Use separating axis theorem to test overlap between triangle and box need to
		 * test for overlap in these directions:
		 * 
		 * 1) The {X, Y, Z} directions (actually, since we use the AABB of the triangle,
		 * we do not even need to test these).
		 * 
		 * 2) Normal of the triangle.
		 * 
		 * 3) Crossproduct (edge from triangle {X, Y, Z} directions), this gives 3x3=9
		 * more tests.
		 */

		// Box and triangle overlaps.
		return 1;
	}
}
