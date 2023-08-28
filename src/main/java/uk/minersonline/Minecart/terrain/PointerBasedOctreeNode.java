package uk.minersonline.Minecart.terrain;

import uk.minersonline.Minecart.core.math.Vec3i;

public class PointerBasedOctreeNode extends OctreeNode{
    public OctreeDrawInfo drawInfo;

    public PointerBasedOctreeNode(Vec3i min, int size, OctreeNodeType type) {
        super(min, size, type);
        drawInfo = new OctreeDrawInfo();
    }
}
