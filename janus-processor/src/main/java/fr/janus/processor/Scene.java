package fr.janus.processor;

import java.util.Iterator;

import fr.alchemy.utilities.collections.array.Array;
import fr.janus.processor.util.AABB;
import fr.janus.processor.util.Vector3f;

public class Scene implements Iterable<Model> {

	private final Array<Model> models = Array.ofType(Model.class);

	public void addModel(Model model) {
		// TODO: Support model instance.
		this.models.add(model);
	}

	public AABB getAABB() {
		if (modelCount() <= 0) {
			throw new IllegalStateException("No models in the scene!");
		}

		Model first = models.first();
		Vector3f max = new Vector3f(first.getAABB().max());
		Vector3f min = new Vector3f(first.getAABB().min());

		for (int i = 1; i < modelCount(); ++i) {
			AABB box = models.get(i).getAABB();
			min.min(box.min());
			max.max(box.max());
		}

		return new AABB(min, max);
	}

	public int modelCount() {
		return models.size();
	}

	@Override
	public Iterator<Model> iterator() {
		return models.iterator();
	}
}
