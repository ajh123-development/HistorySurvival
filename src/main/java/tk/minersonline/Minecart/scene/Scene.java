package tk.minersonline.Minecart.scene;

import tk.minersonline.Minecart.scene.objects.Mesh;
import tk.minersonline.Minecart.scene.views.ProjectionHandler;

import java.util.HashMap;
import java.util.Map;

public class Scene {
    private final Map<String, Mesh> meshMap;
    private final ProjectionHandler projection;

    public Scene(ProjectionHandler projection) {
        this.projection = projection;
        meshMap = new HashMap<>();
    }

    public void addMesh(String meshId, Mesh mesh) {
        meshMap.put(meshId, mesh);
    }

    public void cleanup() {
        meshMap.values().forEach(Mesh::cleanup);
    }

    public Map<String, Mesh> getMeshMap() {
        return meshMap;
    }

    public ProjectionHandler getProjection() {
        return projection;
    }

    public void resize(int width, int height) {
        projection.updateMatrix(width, height);
    }
}
