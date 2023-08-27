package uk.minersonline.Minecart.dc.impl;

import uk.minersonline.Minecart.core.math.Vec3i;
import uk.minersonline.Minecart.dc.OctreeNode;
import uk.minersonline.Minecart.dc.OctreeNodeType;

public class MdcOctreeNode extends OctreeNode {
    public MdcVertex[] vertices;

    public MdcOctreeNode(Vec3i position, int size, OctreeNodeType type) {
        super(position, size, type);
        this.vertices = new MdcVertex[0];
    }
}
