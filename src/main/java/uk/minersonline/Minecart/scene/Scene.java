package uk.minersonline.Minecart.scene;

import uk.minersonline.Minecart.gui.IGuiInstance;
import uk.minersonline.Minecart.scene.objects.Entity;
import uk.minersonline.Minecart.scene.objects.Model;
import uk.minersonline.Minecart.scene.terrain.World;
import uk.minersonline.Minecart.scene.views.ProjectionHandler;

import java.util.HashMap;
import java.util.Map;

public class Scene {
    private final Map<String, Model> modelMap;
    private final TextureCache textureCache;
    private final ProjectionHandler projection;

    private final World world;

    private final Camera camera;
    private IGuiInstance guiInstance;

    public Scene(ProjectionHandler projection) {
        this.projection = projection;
        textureCache = new TextureCache();
        modelMap = new HashMap<>();
        camera = new Camera();
        world = new World();
    }

    public IGuiInstance getGuiInstance() {
        return guiInstance;
    }

    public void setGuiInstance(IGuiInstance guiInstance) {
        this.guiInstance = guiInstance;
    }

    public Camera getCamera() {
        return camera;
    }

    public World getWorld() {
        return world;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public void addEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        if (model == null) {
            throw new RuntimeException("Could not find model [" + modelId + "]");
        }
        model.getEntitiesList().add(entity);
    }

    public void addModel(Model model) {
        modelMap.put(model.getId(), model);
    }

    public void cleanup() {
        modelMap.values().forEach(Model::cleanup);
    }

    public Map<String, Model> getModelMap() {
        return modelMap;
    }

    public ProjectionHandler getProjection() {
        return projection;
    }

    public void resize(int width, int height) {
        projection.updateMatrix(width, height);
    }
}
