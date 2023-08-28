package uk.minersonline.Minecart.terrain.entities;

import uk.minersonline.Minecart.core.buffers.VBO;
import uk.minersonline.Minecart.core.math.Transform;
import uk.minersonline.Minecart.core.math.Vec3f;

public class ModelEntity {
    private VBO vbo;
    private Transform transform;

    public ModelEntity(VBO vbo) {
        this.vbo = vbo;
        transform = new Transform();
    }

    public void setTranslation(Vec3f translation) {
        transform.setTranslation(translation);
    }

    public void setRotation(Vec3f rotation) {
        transform.setRotation(rotation);
    }

    public void setScaling(Vec3f scaling) {
        transform.setScaling(scaling);
    }

    public VBO getVbo() {
        return vbo;
    }

    public Transform getTransform() {
        return transform;
    }
}
