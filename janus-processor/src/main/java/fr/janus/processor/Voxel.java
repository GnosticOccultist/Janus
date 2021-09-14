package fr.janus.processor;

public class Voxel {

	private Status status;

	public Voxel() {
		this.status = Status.EMPTY;
	}
	
	public Voxel(Status status) {
		this.status = status;
	}
	
	public Status status() {
		return status;
	}

	public enum Status {

		EMPTY, SOLID;
	}
}
