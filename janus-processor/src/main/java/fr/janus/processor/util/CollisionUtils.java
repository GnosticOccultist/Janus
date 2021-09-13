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
		float[] v0 = new float[3];
		float[] v1 = new float[3];
		float[] v2 = new float[3];

		float[] e0 = new float[3];
		float[] e1 = new float[3];
		float[] e2 = new float[3];

		/*
		 * This is the fastest branch on Sun move everything so that the box center is
		 * in (0,0,0).
		 */
		sub(v0, triVertices[0], boxCenter);
		sub(v1, triVertices[1], boxCenter);
		sub(v2, triVertices[2], boxCenter);

		// Compute triangle edges.
		sub(e0, v1, v0);
		sub(e1, v2, v1);
		sub(e2, v0, v2);

		/*
		 * Bullet 3: Test the 9 tests first (this was faster).
		 */
		float min = 0, max = 0;
		float fex = Math.abs(e0[0]);
		float fey = Math.abs(e0[1]);
		float fez = Math.abs(e0[2]);
		axisTestX01(v0, v2, boxHalfSize, e0[2], e0[1], fez, fey, min, max);
		axisTestY02(v0, v2, boxHalfSize, e0[2], e0[0], fez, fex, min, max);
		axisTestZ12(v1, v2, boxHalfSize, e0[1], e0[0], fey, fex, min, max);

		fex = Math.abs(e1[0]);
		fey = Math.abs(e1[1]);
		fez = Math.abs(e1[2]);
		axisTestX01(v0, v2, boxHalfSize, e1[2], e1[1], fez, fey, min, max);
		axisTestY02(v0, v2, boxHalfSize, e1[2], e1[0], fez, fex, min, max);
		axisTestZ0(v0, v1, boxHalfSize, e1[1], e1[0], fey, fex, min, max);

		fex = Math.abs(e2[0]);
		fey = Math.abs(e2[1]);
		fez = Math.abs(e2[2]);
		axisTestX2(v0, v1, boxHalfSize, e2[2], e2[1], fez, fey, min, max);
		axisTestY1(v0, v1, boxHalfSize, e2[2], e2[0], fez, fex, min, max);
		axisTestZ12(v1, v2, boxHalfSize, e2[1], e2[0], fey, fex, min, max);

		/*
		 * Bullet 1: First test overlap in the {X, Y, Z} directions. Find min, max of
		 * the triangle in each direction, and test for overlap in. That direction --
		 * this is equivalent to testing a minimal AABB around the triangle against the
		 * AABB.
		 */

		// Test in X-direction.
		findMinMax(v0[0], v1[0], v2[0], min, max);
		if (min > boxHalfSize[0] || max < -boxHalfSize[0]) {
			return 0;
		}

		// Test in Y-direction.
		findMinMax(v0[1], v1[1], v2[1], min, max);
		if (min > boxHalfSize[1] || max < -boxHalfSize[1]) {
			return 0;
		}

		// Test in Z-direction.
		findMinMax(v0[2], v1[2], v2[2], min, max);
		if (min > boxHalfSize[2] || max < -boxHalfSize[2]) {
			return 0;
		}

		/*
		 * Bullet 2: Test if the box intersects the plane of the triangle compute plane
		 * equation of triangle: normal * x + d = 0;
		 */
		float[] normal = new float[3];
		cross(normal, e0, e1);

		if (!planeBoxOverlaps(normal, v0, boxHalfSize)) {
			return 0;
		}

		// Box and triangle overlaps.
		return 1;
	}

	public static boolean planeBoxOverlaps(float[] normal, float[] vertex, float[] maxBox) {
		float vMin[] = new float[3];
		float vMax[] = new float[3];
		float v;
		for (int q = 0; q <= 2; ++q) {
			v = vertex[q];
			if (normal[q] > 0.0F) {
				vMin[q] = -maxBox[q] - v;
				vMax[q] = maxBox[q] - v;
			} else {
				vMin[q] = maxBox[q] - v;
				vMax[q] = -maxBox[q] - v;
			}
		}

		if (dot(normal, vMin) > 0.0F) {
			return false;
		}
		if (dot(normal, vMax) >= 0.0F) {
			return true;
		}

		return false;
	}

	private static float dot(float[] v1, float[] v2) {
		return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
	}

	private static void cross(float[] dest, float[] v1, float[] v2) {
		dest[0] = v1[1] * v2[2] - v1[2] * v2[1];
		dest[1] = v1[2] * v2[0] - v1[0] * v2[2];
		dest[2] = v1[0] * v2[1] - v1[1] * v2[0];
	}

	private static void findMinMax(float x0, float x1, float x2, float min, float max) {
		min = max = x0;
		if (x1 < min) {
			min = x1;
		}
		if (x1 > max) {
			max = x1;
		}
		if (x2 < min) {
			min = x2;
		}
		if (x2 > max) {
			max = x2;
		}
	}

	private static void sub(float[] store, float[] v1, float[] v2) {
		store[0] = v1[0] - v2[0];
		store[1] = v1[1] - v2[1];
		store[2] = v1[2] - v2[2];
	}

	public static float axisTestX01(float[] v0, float[] v2, float[] boxHalfSize, float a, float b, float fa, float fb,
			float min, float max) {
		float p0 = a * v0[1] - b * v0[2];
		float p2 = a * v2[1] - b * v2[2];
		if (p0 < p2) {
			min = p0;
			max = p2;
		} else {
			min = p2;
			max = p0;
		}
		float rad = fa * boxHalfSize[1] + fb * boxHalfSize[2];
		if (min > rad || max < -rad) {
			return 0;
		}

		return -1;
	}

	public static float axisTestX2(float[] v0, float[] v1, float[] boxHalfSize, float a, float b, float fa, float fb,
			float min, float max) {
		float p0 = a * v0[1] - b * v0[2];
		float p1 = a * v1[1] - b * v1[2];
		if (p0 < p1) {
			min = p0;
			max = p1;
		} else {
			min = p1;
			max = p0;
		}
		float rad = fa * boxHalfSize[1] + fb * boxHalfSize[2];
		if (min > rad || max < -rad) {
			return 0;
		}

		return -1;
	}

	public static float axisTestY02(float[] v0, float[] v2, float[] boxHalfSize, float a, float b, float fa, float fb,
			float min, float max) {
		float p0 = -a * v0[0] + b * v0[2];
		float p2 = -a * v2[0] + b * v2[2];
		if (p0 < p2) {
			min = p0;
			max = p2;
		} else {
			min = p2;
			max = p0;
		}
		float rad = fa * boxHalfSize[0] + fb * boxHalfSize[2];
		if (min > rad || max < -rad) {
			return 0;
		}

		return -1;
	}

	public static float axisTestY1(float[] v0, float[] v1, float[] boxHalfSize, float a, float b, float fa, float fb,
			float min, float max) {
		float p0 = -a * v0[0] + b * v0[2];
		float p1 = -a * v1[0] + b * v1[2];
		if (p0 < p1) {
			min = p0;
			max = p1;
		} else {
			min = p1;
			max = p0;
		}
		float rad = fa * boxHalfSize[0] + fb * boxHalfSize[2];
		if (min > rad || max < -rad) {
			return 0;
		}

		return -1;
	}

	public static float axisTestZ12(float[] v1, float[] v2, float[] boxHalfSize, float a, float b, float fa, float fb,
			float min, float max) {
		float p1 = a * v1[0] - b * v1[1];
		float p2 = a * v2[0] - b * v2[1];
		if (p2 < p1) {
			min = p2;
			max = p1;
		} else {
			min = p1;
			max = p2;
		}
		float rad = fa * boxHalfSize[0] + fb * boxHalfSize[1];
		if (min > rad || max < -rad) {
			return 0;
		}

		return -1;
	}

	public static float axisTestZ0(float[] v0, float[] v1, float[] boxHalfSize, float a, float b, float fa, float fb,
			float min, float max) {
		float p0 = a * v0[0] - b * v0[1];
		float p1 = a * v1[0] - b * v1[1];
		if (p0 < p1) {
			min = p0;
			max = p1;
		} else {
			min = p1;
			max = p0;
		}
		float rad = fa * boxHalfSize[0] + fb * boxHalfSize[2];
		if (min > rad || max < -rad) {
			return 0;
		}

		return -1;
	}
}
