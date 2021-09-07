package fr.janus.processor;

import fr.alchemy.utilities.collections.array.Array;
import fr.alchemy.utilities.logging.FactoryLogger;
import fr.alchemy.utilities.logging.Logger;
import fr.janus.processor.Scene.ModelInstance;
import fr.janus.processor.Scene.ModelType;
import fr.janus.processor.util.AABB;
import fr.janus.processor.util.CollisionUtils;
import fr.janus.processor.util.Triangle;
import fr.janus.processor.util.Vector3f;
import fr.janus.processor.util.Vector3i;

public class PVSGenerator {

	private static final Logger logger = FactoryLogger.getLogger("janus-processor.generator");
	
	private final Scene scene;

	public PVSGenerator(Scene scene) {
		this.scene = scene;
	}
	
	public void generate() {
		logger.info("Begin generating PVS.");
		
		AABB sceneBounds = scene.getAABB();
		logger.info("Scene AABB: " + sceneBounds + ".");
		
		Vector3f distance = sceneBounds.size();
		
		int cellCountX = (int) distance.x();
		int cellCountY = (int) distance.y();
		int cellCountZ = (int) distance.z();
	
		// Create a voxel container of specified dimensions.
		VoxelContainer container = new VoxelContainer(new Vector3f(1, 1, 1), 
				new Vector3i(cellCountX, cellCountY, cellCountZ));
		
		logger.info("Begin voxelizing models.");
		
		// Voxelize all the models.
		voxelizeModels(container, sceneBounds);
		
		logger.info("End voxelizing models.");
	}

	protected void voxelizeModels(VoxelContainer container, AABB sceneBounds) {
		float[] boxHalfSize = new float[3];
		// Calculate the voxel's half size in X, Y and Z axis.
		boxHalfSize[0] = container.voxelSize().x() / 2.0F;
		boxHalfSize[1] = container.voxelSize().y() / 2.0F;
		boxHalfSize[2] = container.voxelSize().z() / 2.0F;

		for (ModelInstance model : scene) {

			// Only voxelize occluder models.
			if (model.type() == ModelType.OCCLUDEE) {
				continue;
			}

			Array<Triangle> triangles = Array.ofType(Triangle.class);
			AABB modelBounds = model.getBounds();
			// Get all the voxels AABB that are bounding the model. This will be used as
			// potential voxels to test for intersection.
			Array<AABB> boxes = container.getAllVoxelAABBFromVolume(modelBounds, sceneBounds);

			float[][] triVertices = new float[3][3];
			float[] boxCenter = new float[3];

			// For each triangle of the model.
			for (int i = 0; i < triangles.size(); ++i) {

				// Store the triangle in a format that the collision algorithm can receive.
				triVertices[0][0] = triangles.get(i).getA().x();
				triVertices[0][1] = triangles.get(i).getA().y();
				triVertices[0][2] = triangles.get(i).getA().z();
				
				triVertices[1][0] = triangles.get(i).getB().x();
				triVertices[1][1] = triangles.get(i).getB().y();
				triVertices[1][2] = triangles.get(i).getB().z();
				
				triVertices[2][0] = triangles.get(i).getC().x();
				triVertices[2][1] = triangles.get(i).getC().y();
				triVertices[2][2] = triangles.get(i).getC().z();
				
				// For all the potential voxels that can intersect the model.
				for (int j = 0; j < boxes.size(); ++j) {
					// Find the voxel center.
					AABB box = boxes.get(j);
					Vector3f midpoint = new Vector3f();
					box.max().add(box.min(), midpoint);
					midpoint.div(2.0F);
					
					boxCenter[0] = midpoint.x();
					boxCenter[1] = midpoint.y();
					boxCenter[2] = midpoint.z();
					
					// Test for intersection between the triangle and the voxel's AABB.
					if (CollisionUtils.triBoxOverlaps(boxCenter, boxHalfSize, triVertices) == 1) {
						// Voxel is solid.
						container.addVoxelAt(container.getVoxelIndexFromPoint(midpoint, sceneBounds));
					}
				}
			}
		}
	}
}
