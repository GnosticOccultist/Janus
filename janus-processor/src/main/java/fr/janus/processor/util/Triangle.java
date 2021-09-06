package fr.janus.processor.util;

public class Triangle {

	private final Vector3f a, b, c;

	public Triangle() {
		this.a = new Vector3f();
		this.b = new Vector3f();
		this.c = new Vector3f();
	}

	public Triangle(Vector3f a, Vector3f b, Vector3f c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public Vector3f getA() {
		return a;
	}

	public Vector3f getB() {
		return b;
	}

	public Vector3f getC() {
		return c;
	}
}
