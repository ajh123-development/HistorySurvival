package uk.minersonline.Minecart.terrain.entities;

import uk.minersonline.Minecart.core.math.Vec3f;
import uk.minersonline.Minecart.core.model.Vertex;

public class MeshVertex extends Vertex {
    public MeshVertex(){
    }

    public MeshVertex(Vec3f pos, Vec3f norm, Vec3f color)
    {
        super();
        this.setPos(pos);
        this.setNormal(norm);
        this.setColor(color);
    }
}
