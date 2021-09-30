package fr.janus.processor;

import fr.alchemy.utilities.collections.array.Array;
import fr.janus.processor.util.Vector3i;

public class SceneTile {

	private final Vector3i from;
	private final Vector3i to;
	
	private int voxelCount;
	
	private final Array<Cell> cells = Array.ofType(Cell.class);

	public SceneTile(Vector3i from, Vector3i to) {
		this.from = from;
		this.to = to;
	}
	
	public void addCell(Cell cell) {
		this.cells.add(cell);
	}
	
	public Array<Cell> cells() {
		return cells;
	}
	
	public Vector3i from() {
		return from;
	}
	
	public Vector3i to() {
		return to;
	}

	public void increaseVoxelCount() {
		this.voxelCount++;
	}
	
	public int voxelCount() {
		return voxelCount;
	}
	
	public int numberOfVoxelsInside() {
		return (to.x() - from.x() + 1) * (to.y() - from.y() + 1) * (to.z() - from.z() + 1);
	}
}
