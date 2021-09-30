package fr.janus.processor;

import java.util.function.Consumer;
import java.util.function.Predicate;

import fr.alchemy.utilities.collections.array.Array;
import fr.janus.processor.Voxel.Status;
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

	public void forEach(Vector3i from, Vector3i to, Consumer<Voxel> consumer) {
		forEach(from, to, consumer, null);
	}

	public void forEach(Vector3i from, Vector3i to, Consumer<Voxel> consumer, Predicate<Voxel> filter) {
		Vector3i voxelPos = new Vector3i();
		for (var x = from.x(); x <= to.x(); ++x) {
			for (var y = from.y(); y <= to.y(); ++y) {
				for (var z = from.z(); z <= to.z(); ++z) {
					voxelPos.set(x, y, z);
					Voxel voxel = voxelAt(voxelPos);
					if (filter == null || filter.test(voxel)) {
						consumer.accept(voxel);
					}
				}
			}
		}
	}

	public Voxel voxelAt(Vector3i voxelIndex) {
		// Check valid position.
		if (isPositionOutOfBounds(voxelIndex)) {
			throw new RuntimeException("Voxel position " + voxelIndex + " is invalid!");
		}

		// If exists voxel in that place return it. Otherwise return empty Voxel.
		if (voxels.contains(voxelIndex)) {
			return new Voxel(Status.SOLID);
		}

		return new Voxel(Status.EMPTY);
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

	public int solidVoxelCount() {
		return voxels.size();
	}

	public enum Direction {
		POSITIVE_X(1, 0, 0), 
		NEGATIVE_X(-1, 0, 0), 
		POSITIVE_Y(0, 1, 0), 
		NEGATIVE_Y(0, -1, 0), 
		POSITIVE_Z(0, 0, 1), 
		NEGATIVE_Z(0, 0, -1);
		
		Vector3i offset;
		
		private Direction(int x, int y, int z) {
			this.offset = new Vector3i(x, y, z);
		}
		
		public Vector3i offset() {
			return offset;
		}
		
		public boolean isPositive() {
			return this == POSITIVE_X || this == POSITIVE_Y || this == POSITIVE_Z;
		}
	}
}
