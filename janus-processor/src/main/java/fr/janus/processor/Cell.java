package fr.janus.processor;

import fr.alchemy.utilities.collections.array.Array;
import fr.janus.processor.util.Vector3i;

public class Cell {

	private final Array<Integer> models;
	
	private final Array<Cell> visibleCells;
	
	private final Vector3i minPoint, maxPoint;
	
	public Cell(Vector3i minPoint, Vector3i maxPoint) {
		this.minPoint = minPoint;
		this.maxPoint = maxPoint;
		this.models = Array.ofType(Integer.class);
		this.visibleCells = Array.ofType(Cell.class);
	}
	
	public Vector3i minPoint() {
		return minPoint;
	}
	
	public Vector3i maxPoint() {
		return maxPoint;
	}
}
