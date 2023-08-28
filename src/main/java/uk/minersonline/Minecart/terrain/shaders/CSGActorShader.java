package uk.minersonline.Minecart.terrain.shaders;

import uk.minersonline.Minecart.core.scene.GameObject;
import uk.minersonline.Minecart.core.shaders.Shader;
import uk.minersonline.Minecart.core.utils.ResourceLoader;
import uk.minersonline.Minecart.terrain.entities.ModelEntity;

public class CSGActorShader extends Shader {
    private static CSGActorShader instance = null;

    public static CSGActorShader getInstance() {
        if(instance == null) {
            instance = new CSGActorShader();
        }
        return instance;
    }

    private CSGActorShader(){
        super();
        addVertexShader(ResourceLoader.loadShader("shaders/csgAction.glsl"));
        addFragmentShader(ResourceLoader.loadShader("shaders/csgAction.frag"));
        compileShader();

        addUniform("worldMatrix");
        //addUniform("worldMatrix");
    }

    public void updateUniforms(GameObject object) {
        //setUniform("modelViewProjectionMatrix", object.getTransform().getModelViewProjectionMatrix());
    }

    public void updateTransform(ModelEntity modelEntity) {
        setUniform("worldMatrix", modelEntity.getTransform().getModelViewProjectionMatrix());
        //setUniform("worldMatrix", transform.getWorldMatrix());
    }
}