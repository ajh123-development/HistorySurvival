package tk.minersonline.Minecart.scene.objects;

import java.util.*;

public class Model {
	private final String id;
	private final List<Entity> entitiesList;
	private final List<Mesh> meshList;

	public Model(String id, List<Mesh> meshList) {
		this.id = id;
		this.meshList = meshList;
		this.entitiesList = new ArrayList<>();
	}

	public void cleanup() {
		meshList.forEach(Mesh::cleanup);
	}
	public List<Entity> getEntitiesList() {
		return entitiesList;
	}

	public String getId() {
		return id;
	}

	public List<Mesh> getMeshList() {
		return meshList;
	}
}
