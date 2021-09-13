package fr.janus.processor;

import java.util.Iterator;

import fr.alchemy.utilities.collections.array.Array;
import fr.janus.processor.Scene.ModelInstance;
import fr.janus.processor.util.AABB;
import fr.janus.processor.util.Vector3f;
import fr.janus.processor.util.Vector3i;

public class Scene implements Iterable<ModelInstance> {

	public static final int DEFAULT_TILE_SIZE = 10;

	private final Array<ModelInstance> models = Array.ofType(ModelInstance.class);

	public void addModel(Model model, ModelType type) {
		// TODO: Support model instance.
		this.models.add(new ModelInstance(model, type));
	}

	public AABB getAABB() {
		if (modelCount() <= 0) {
			throw new IllegalStateException("No models in the scene!");
		}

		Model first = models.first().model;
		Vector3f max = new Vector3f(first.getAABB().max());
		Vector3f min = new Vector3f(first.getAABB().min());

		for (int i = 1; i < modelCount(); ++i) {
			AABB box = models.get(i).model.getAABB();
			min.min(box.min());
			max.max(box.max());
		}

		return new AABB(min, max);
	}

	public int modelCount() {
		return models.size();
	}

	@Override
	public Iterator<ModelInstance> iterator() {
		return models.iterator();
	}

	public enum ModelType {

		OCCLUDEE,

		OCCLUDER;
	}

	public static class Options {

		Vector3f voxelSize;
		Vector3i maxCellSize;
		Vector3i sceneTileSize;

		public Options() {
			this.voxelSize = new Vector3f(DEFAULT_TILE_SIZE / 10, DEFAULT_TILE_SIZE / 10, DEFAULT_TILE_SIZE / 10);
			this.maxCellSize = new Vector3i(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
			this.sceneTileSize = new Vector3i(DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE);
		}
	}

	public class ModelInstance {

		Model model;

		ModelType type;

		public ModelInstance(Model model, ModelType type) {
			this.model = model;
			this.type = type;
		}

		public ModelType type() {
			return type;
		}

		public AABB getBounds() {
			return model.getAABB();
		}
	}
}
