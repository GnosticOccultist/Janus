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
	
	public Triangle set(Triangle other) {
		a.set(other.a);
		b.set(other.b);
		c.set(other.c);	
		return this;
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

	public Triangle transform(Matrix4f transform, Triangle store) {
		var result = store != null ? store : new Triangle();
		result.set(this);
		return result.transform(transform);
	}

	public Triangle transform(Matrix4f transform) {
		a.transform(transform);
		b.transform(transform);
		c.transform(transform);
		return this;
	}
}
