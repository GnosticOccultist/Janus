package fr.janus.processor;

import java.util.Iterator;
import java.util.stream.Stream;

import fr.alchemy.utilities.collections.array.Array;
import fr.janus.processor.util.AABB;
import fr.janus.processor.util.Matrix4f;
import fr.janus.processor.util.Vector3f;
import fr.janus.processor.util.Vector3i;

public class Scene implements Iterable<ModelInstance> {

	public static final int MAX_NUMBER_OF_MODELS = 1048576;

	public static final int DEFAULT_TILE_SIZE = 10;

	private Options options;

	private final Array<SceneTile> tiles = Array.ofType(SceneTile.class);

	private final Array<ModelInstance> instances = Array.ofType(ModelInstance.class);

	public Scene() {
		this.options = new Options();
	}

	public void addModel(Model model, int id, ModelType type) {
		this.instances.add(new ModelInstance(model, id, type));
	}

	public void addModel(Model model, int id, Matrix4f transform, ModelType type) {
		this.instances.add(new ModelInstance(model, id, transform, type));
	}

	public void addTile(SceneTile tile) {
		this.tiles.add(tile);
	}

	public AABB getAABB() {
		if (modelCount() <= 0) {
			throw new IllegalStateException("No models in the scene!");
		}

		var first = instances.first();
		var max = new Vector3f(first.getBounds().max());
		var min = new Vector3f(first.getBounds().min());

		for (int i = 1; i < modelCount(); ++i) {
			var box = instances.get(i).getBounds();
			min.min(box.min());
			max.max(box.max());
		}

		return new AABB(min, max);
	}

	public Options options() {
		return options;
	}

	public int modelCount() {
		return instances.size();
	}

	public Stream<SceneTile> streamTiles() {
		return tiles.parallelStream();
	}

	@Override
	public Iterator<ModelInstance> iterator() {
		return instances.iterator();
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
}
