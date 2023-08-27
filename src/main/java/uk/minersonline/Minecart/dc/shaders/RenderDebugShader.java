package uk.minersonline.Minecart.dc.shaders;

import uk.minersonline.Minecart.core.scene.GameObject;
import uk.minersonline.Minecart.core.shaders.Shader;
import uk.minersonline.Minecart.core.utils.ResourceLoader;

public class RenderDebugShader extends Shader {
    private static RenderDebugShader instance = null;

    public static RenderDebugShader getInstance() {
        if(instance == null) {
            instance = new RenderDebugShader();
        }
        return instance;
    }

    private RenderDebugShader(){
        super();
        addVertexShader(ResourceLoader.loadShader("shaders/debug.vert"));
        addFragmentShader(ResourceLoader.loadShader("shaders/debug.frag"));
        compileShader();

        addUniform("MVP");
    }

    public void updateUniforms(GameObject object) {
        setUniform("MVP", object.getTransform().getModelViewProjectionMatrix());
    }
}
