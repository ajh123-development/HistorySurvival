package uk.minersonline.Minecart.dc;

import uk.minersonline.Minecart.core.math.Vec3i;
import uk.minersonline.Minecart.core.physics.WorldCollisionNode;

import java.util.List;

public class ChunkNode {
    public Vec3i min;
    public int size;
    public boolean	active = false;
    public boolean	invalidated = false;
    public boolean	empty = false;
    public List<OctreeNode> chunkBorderNodes;
    public RenderMesh renderMesh, seamMesh;
    public WorldCollisionNode worldNode;
    public boolean canBeSelected = false;
    public boolean chunkCSGEdited = false;
    public boolean chunkIsChanged = false;
    public ReduceStateEnum reduceStatus = ReduceStateEnum.INITIAL;
    public long chunkCode;

    public ChunkNode(){
        min = new Vec3i(0, 0, 0);
        size = 0;
        worldNode = new WorldCollisionNode();
    }

    private boolean pointIsInCube(Vec3i p, float x_min, float y_min, float z_min, float size) {
        return  (p.x >= x_min && p.y >= y_min && p.z >= z_min) &&
                (p.x <= x_min + size && p.y <= y_min + size && p.z <= z_min + size);
    }

    public boolean pointIsInside(Vec3i p){
        return pointIsInCube(p, min.x, min.y, min.z, size);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChunkNode node = (ChunkNode) o;

        if (size != node.size) return false;
        if (active != node.active) return false;
        return min.equals(node.min);
    }

    @Override
    public int hashCode() {
        int result = min.hashCode();
        result = 31 * result + size;
        result = 31 * result + (active ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChunkNode{" +
                "min=" + min +
                ", size=" + size +
                '}';
    }
}
