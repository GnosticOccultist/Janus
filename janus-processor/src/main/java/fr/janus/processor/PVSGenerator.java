package fr.janus.processor;

import fr.alchemy.utilities.collections.array.Array;
import fr.alchemy.utilities.logging.FactoryLogger;
import fr.alchemy.utilities.logging.Logger;
import fr.janus.processor.Scene.ModelInstance;
import fr.janus.processor.Scene.ModelType;
import fr.janus.processor.Voxel.Status;
import fr.janus.processor.util.AABB;
import fr.janus.processor.util.CollisionUtils;
import fr.janus.processor.util.Triangle;
import fr.janus.processor.util.Vector3f;
import fr.janus.processor.util.Vector3i;

public class PVSGenerator {

	private static final Logger logger = FactoryLogger.getLogger("janus-processor.generator");

	private final Scene scene;

	private VoxelContainer container;

	public PVSGenerator(Scene scene) {
		this.scene = scene;
	}

	public void generate() {
		logger.info("Begin generating PVS.");

		AABB sceneBounds = scene.getAABB();
		logger.info("Scene AABB: " + sceneBounds + ".");

		Vector3f distance = sceneBounds.size();
		Vector3f cellSize = scene.options().voxelSize;

		int cellCountX = (int) (distance.x() / cellSize.x());
		int cellCountY = (int) (distance.y() / cellSize.y());
		int cellCountZ = (int) (distance.z() / cellSize.z());

		// Create a voxel container of specified dimensions.
		container = new VoxelContainer(new Vector3f(1, 1, 1), new Vector3i(cellCountX, cellCountY, cellCountZ));

		logger.info("Begin voxelizing models.");

		// Voxelize all the models.
		voxelizeModels(container, sceneBounds);

		logger.info("End voxelizing models.");

		logger.info("Solid voxel count: " + container.solidVoxelCount() + ".");

		logger.info("Begin creating scene tiles.");
		// Subdivide the scene into tiles.
		createSceneTiles();
		logger.info("End creating scene tiles.");
		
		Array<Cell> cells = Array.ofType(Cell.class);
		
		logger.info("Begin generating cells.");
		generateCellsFromEmptyVoxelsUsingTiles(cells);
		logger.info("End generating cells.");
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

	protected void createSceneTiles() {
		// Get the scene tile size.
		Vector3i tileSize = scene.options().sceneTileSize;

		// Calculate how many tiles needed based on the scene number of voxels.
		Vector3i numberOfTiles = new Vector3i((int) (container.voxelCount().x() / tileSize.x()),
				(int) (container.voxelCount().y() / tileSize.y()), (int) (container.voxelCount().z() / tileSize.z()));

		Vector3i from = new Vector3i(), to = new Vector3i();
		// Create the scene tiles.
		for (var x = 0; x < numberOfTiles.x(); ++x) {
			from.setX(x * tileSize.x());
			to.setX(x * tileSize.x() + tileSize.x() - 1);

			// Clamp to the bounds.
			to.setX(Math.min(to.x(), container.voxelCount().x() - 1));
			for (var y = 0; y < numberOfTiles.y(); ++y) {
				from.setY(y * tileSize.y());
				to.setY(y * tileSize.y() + tileSize.y() - 1);
				
				to.setY(Math.min(to.y(), container.voxelCount().y() - 1));
				for (var z = 0; z < numberOfTiles.z(); ++z) {
					from.setZ(z * tileSize.z());
					to.setZ(z * tileSize.z() + tileSize.z() - 1);
					
					to.setZ(Math.min(to.z(), container.voxelCount().z() - 1));
					
					// Create the scene tile from the start and end voxel points.
					SceneTile tile = new SceneTile(from, to);
					
					// Add the number of solid voxels to the cell.
					// It will be used later when checking if we covered all voxels with cells.
					container.forEach(from, to, v -> tile.increaseVoxelCount(), v -> v.status() == Status.SOLID);

					scene.addTile(tile);
				}
			}
		}
	}
	
	protected void generateCellsFromEmptyVoxelsUsingTiles(Array<Cell> cells) {
		// Process scene tiles in parallel. Each one has it's own collection of cells and can read the voxel container concurrently.
		scene.streamTiles().forEach(tile -> {
			
			// How many empty voxels are we expecting to have in this tile.
			int expectedEmptyVoxels = tile.numberOfVoxelsInside() - tile.voxelCount();
			
			// If all the tile is solid, don't generate a cell.
			if (expectedEmptyVoxels == 0) {
				return;
			}
			
			// Do while there are no pending empty voxels left in the tile.
			while (expectedEmptyVoxels > 0) {
				
				// Take first seed voxel index.
				Vector3i minVoxelPoint = 
			}
		});
	}
}
