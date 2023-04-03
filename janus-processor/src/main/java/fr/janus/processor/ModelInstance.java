package fr.janus.processor;

import fr.alchemy.utilities.Validator;
import fr.janus.processor.Scene.ModelType;
import fr.janus.processor.util.AABB;
import fr.janus.processor.util.Matrix4f;
import fr.janus.processor.util.Triangle;

public class ModelInstance {

	private final Model model;

	private final int id;

	private final Matrix4f transform;

	private final ModelType type;

	private final AABB bounds;

	public ModelInstance(Model model, int id, ModelType type) {
		this(model, id, new Matrix4f(), type);
	}

	public ModelInstance(Model model, int id, Matrix4f transform, ModelType type) {
		Validator.inRange(id, "The given id " + id + " is out of bounds!", 0, Scene.MAX_NUMBER_OF_MODELS);
		this.model = model;
		this.id = id;
		this.transform = transform;
		this.type = type;
		this.bounds = AABB.undefinedBounds();

		calculateBounds();
	}

	private void calculateBounds() {
		for (var i = 0; i < model.triangleCount(); ++i) {
			var tri = getTriangle(i);

			bounds.min().min(tri.getA());
			bounds.min().min(tri.getB());
			bounds.min().min(tri.getC());

			bounds.max().max(tri.getA());
			bounds.max().max(tri.getB());
			bounds.max().max(tri.getC());
		}
	}

	public Triangle getTriangle(int index) {
		var source = model.getTriangle(index);
		return source.transform(transform, null);
	}

	public Model model() {
		return model;
	}

	public int id() {
		return id;
	}

	public Matrix4f transform() {
		return transform;
	}

	public ModelType type() {
		return type;
	}

	public AABB getBounds() {
		return bounds;
	}
}
