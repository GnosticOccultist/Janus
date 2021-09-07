package fr.janus.processor;

import fr.alchemy.utilities.collections.array.Array;
import fr.janus.processor.util.AABB;
import fr.janus.processor.util.Vector3f;
import fr.janus.processor.util.Vector3i;

public class VoxelContainer {

	private final Vector3f voxelSize;
	private final Vector3i voxelCount;
	
	private final Array<Vector3i> voxels = Array.ofType(Vector3i.class);

	public VoxelContainer(Vector3f voxelSize, Vector3i voxelCount) {
		this.voxelSize = voxelSize;
		this.voxelCount = voxelCount;
	}

	public void addVoxelAt(Vector3i voxelIndex) {
		// Check valid position.
		if (isPositionOutOfBounds(voxelIndex)) {
			throw new RuntimeException("Voxel index " + voxelIndex + " is invalid!");
		}
		
		// Add if not already exists.
		if (!voxels.contains(voxelIndex)) {
			voxels.add(voxelIndex);
		}
	}

	public Array<AABB> getAllVoxelAABBFromVolume(AABB objectBounds, AABB worldBounds) {

		// Get the min and max points.
		Vector3i fromVoxelPos = getVoxelIndexFromPoint(objectBounds.min(), worldBounds);
		Vector3i toVoxelPos = getVoxelIndexFromPoint(objectBounds.max(), worldBounds);

		Array<AABB> voxels = Array.ofType(AABB.class);

		for (int z = fromVoxelPos.z(); z <= toVoxelPos.z(); ++z) {
			for (int y = fromVoxelPos.y(); y <= toVoxelPos.y(); ++y) {
				for (int x = fromVoxelPos.x(); x <= toVoxelPos.x(); ++x) {
					voxels.add(getVoxelAABBFromIndex(new Vector3i(x, y, z), worldBounds));
				}
			}
		}

		return voxels;
	}

	private AABB getVoxelAABBFromIndex(Vector3i voxelIndex, AABB worldBounds) {
		// Check valid position.
		if (isPositionOutOfBounds(voxelIndex)) {
			throw new RuntimeException("Voxel index " + voxelIndex + " out of bounds!");
		}

		Vector3f from = new Vector3f(voxelIndex.x() * voxelSize.x() + worldBounds.min().x(),
				voxelIndex.y() * voxelSize.y() + worldBounds.min().y(),
				voxelIndex.z() * voxelSize.z() + worldBounds.min().z());

		Vector3f to = new Vector3f();
		from.add(voxelSize, to);

		return new AABB(from, to);
	}

	protected Vector3i getVoxelIndexFromPoint(Vector3f pointPosition, AABB worldBounds) {
		Vector3i voxelIndex = new Vector3i((int) ((pointPosition.x() - worldBounds.min().x()) / voxelSize.x()),
				(int) ((pointPosition.y() - worldBounds.min().y()) / voxelSize.y()),
				(int) ((pointPosition.z() - worldBounds.min().z()) / voxelSize.z()));
		// Check valid position.
		if (isPositionOutOfBounds(voxelIndex)) {
			throw new RuntimeException("Point " + voxelIndex + " out of voxel container!");
		}

		return voxelIndex;
	}

	private boolean isPositionOutOfBounds(Vector3i position) {
		return (position.x() < 0 || position.y() < 0 || position.z() < 0 || position.x() >= voxelCount.x()
				|| position.y() >= voxelCount.y() || position.z() >= voxelCount.z());
	}

	public Vector3f voxelSize() {
		return voxelSize;
	}

	public Vector3i voxelCount() {
		return voxelCount;
	}
}
