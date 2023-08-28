package uk.minersonline.Minecart.terrain.impl;

import uk.minersonline.Minecart.core.math.Vec3i;
import uk.minersonline.Minecart.terrain.OctreeNode;
import uk.minersonline.Minecart.terrain.OctreeNodeType;

public class MdcOctreeNode extends OctreeNode {
    public MdcVertex[] vertices;

    public MdcOctreeNode(Vec3i position, int size, OctreeNodeType type) {
        super(position, size, type);
        this.vertices = new MdcVertex[0];
    }
}
