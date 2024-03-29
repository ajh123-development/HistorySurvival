package uk.minersonline.Minecart.terrain;

import uk.minersonline.Minecart.core.math.Vec3f;
import uk.minersonline.Minecart.core.math.Vec3i;
import uk.minersonline.Minecart.core.math.Vec4f;
import uk.minersonline.Minecart.core.utils.BufferUtil;
import uk.minersonline.Minecart.core.utils.Constants;
import uk.minersonline.Minecart.terrain.csg.ICSGOperations;
import uk.minersonline.Minecart.terrain.entities.CSGOperationInfo;
import uk.minersonline.Minecart.terrain.entities.MeshBuffer;
import uk.minersonline.Minecart.terrain.entities.MeshVertex;
import uk.minersonline.Minecart.terrain.impl.MeshGenerationContext;
import uk.minersonline.Minecart.terrain.utils.VoxelHelperUtils;

import java.util.*;

import static uk.minersonline.Minecart.terrain.OctreeNodeType.Node_Internal;
import static uk.minersonline.Minecart.terrain.OctreeNodeType.Node_Leaf;
import static uk.minersonline.Minecart.terrain.VoxelOctree.CHILD_MIN_OFFSETS;
import static uk.minersonline.Minecart.terrain.VoxelOctree.edgevmap;
import static uk.minersonline.Minecart.terrain.utils.SimplexNoise.getNoise;

public abstract class AbstractDualContouring implements DualContouring{
    protected MeshGenerationContext meshGen;
    private final ICSGOperations csgOperationsProcessor;

    public ICSGOperations getCsgOperationsProcessor() {
        return csgOperationsProcessor;
    }

    public AbstractDualContouring(MeshGenerationContext meshGenerationContext, ICSGOperations csgOperations) {
        this.meshGen = meshGenerationContext;
        this.csgOperationsProcessor = csgOperations;
    }

    private List<OctreeNode> constructParents(List<OctreeNode> nodes, Vec3i rootMin, int parentSize) {
        Map<Vec3i, OctreeNode> parentsHash = new HashMap<>();
        for (OctreeNode node : nodes) {
            Vec3i localPos = node.min.sub(rootMin);
            Vec3i parentPos = node.min.sub(new Vec3i(localPos.x % parentSize, localPos.y % parentSize, localPos.z % parentSize));
            OctreeNode parent = parentsHash.get(parentPos);
            if (parent == null) {
                parent = createParent(parentPos, parentSize, OctreeNodeType.Node_Internal);
                parentsHash.put(parentPos, parent);
            }
            for (int j = 0; j < 8; j++) {
                Vec3i childMin = parentPos.add(CHILD_MIN_OFFSETS[j].mul(parentSize / 2));
                if (childMin.equals(node.min)) {
                    parent.children[j] = node;
                    node.child_index = j;
                    break;
                }
            }
        }
        return new ArrayList<>(parentsHash.values());
    }

    protected OctreeNode createParent(Vec3i min, int size, OctreeNodeType type){
        return new PointerBasedOctreeNode(min, size, OctreeNodeType.Node_Internal);
    }

    protected OctreeNode constructTreeUpwards(List<OctreeNode> inputNodes, Vec3i rootMin, int rootNodeSize) {
        List<OctreeNode> sortedNodes = new ArrayList<>(inputNodes);
        sortedNodes.sort(Comparator.comparingInt((OctreeNode lhs) -> lhs.size));
        while (sortedNodes.get(0).size != sortedNodes.get(sortedNodes.size() - 1).size) {
            int iter = 0;
            int size = sortedNodes.get(iter).size;
            do {
                ++iter;
            } while (sortedNodes.get(iter).size == size);

            List<OctreeNode> newNodes = constructParents(sortedNodes.subList(0, iter), rootMin, size * 2);
            newNodes.addAll(sortedNodes.subList(iter, sortedNodes.size()));
            sortedNodes.clear();
            sortedNodes.addAll(newNodes);
            newNodes.clear();
        }

        int parentSize = (sortedNodes.get(0).size) * 2;
        while (parentSize <= rootNodeSize*2) {
            sortedNodes = constructParents(sortedNodes, rootMin, parentSize);
            parentSize *= 2;
        }
        if (sortedNodes.size()!=1){
            throw new IllegalStateException("Incorrect octree!");
        }
        if (!(rootMin.x==sortedNodes.get(0).min.x) || !(rootMin.y==sortedNodes.get(0).min.y)|| !(rootMin.z==sortedNodes.get(0).min.z)){
            throw new IllegalStateException("returned root not equal to input root!");
        }
        return sortedNodes.get(0);
    }

    private void ContourProcessEdge(OctreeNode[] node, int dir, List<Integer> indexBuffer)
    {
        int minSize = 1000000;		// arbitrary big number
        int minIndex = 0;
        int[] indices = { -1, -1, -1, -1 };
        boolean flip = false;
        boolean[] signChange = { false, false, false, false };

        for (int i = 0; i < 4; i++)
        {
            if (node[i].Type != Node_Internal)
            {
                int edge = processEdgeMask[dir][i];
                int c0 = edgevmap[edge][0];
                int c1 = edgevmap[edge][1];
                int m0 = (node[i].corners >> c0) & 1;
                int m1 = (node[i].corners >> c1) & 1;

                if (node[i].size < minSize)
                {
                    minSize = node[i].size;
                    minIndex = i;
                    flip = m1 != 1;
                }

                indices[i] = node[i].index;
                //signChange[i] = (m0 && !m1) || (!m0 && m1);
                signChange[i] =
                        (m0 == meshGen.MATERIAL_AIR && m1 != meshGen.MATERIAL_AIR) ||
                                (m0 != meshGen.MATERIAL_AIR && m1 == meshGen.MATERIAL_AIR);
            }
        }

        if (!signChange[minIndex]) {
            return;
        }

        if (!flip) {
            indexBuffer.add(indices[0]);
            indexBuffer.add(indices[1]);
            indexBuffer.add(indices[3]);

            indexBuffer.add(indices[0]);
            indexBuffer.add(indices[3]);
            indexBuffer.add(indices[2]);
        }
        else {
            indexBuffer.add(indices[0]);
            indexBuffer.add(indices[3]);
            indexBuffer.add(indices[1]);

            indexBuffer.add(indices[0]);
            indexBuffer.add(indices[2]);
            indexBuffer.add(indices[3]);
        }
    }

    private Vec3i chunkMinForPosition(Vec3i min, int size) {
        // http://ngildea.blogspot.com/2015/07/fixing-seams-bug.html
        int mask = ~(size - 1);
        return new Vec3i(min.x & mask, min.y & mask, min.z & mask);
    }

    private void ContourEdgeProc(OctreeNode[] node, int dir, List<Integer> buffer, boolean isSeam, int chunkSize) {
        if (node[0] == null || node[1] == null || node[2] == null || node[3] == null) {
            return;
        }

        // bit of a hack but it works: prevent overlapping seams by only processing edges that stradle multiple chunks
        if(isSeam) {
            Set<Vec3i> chunks = new HashSet<>();
            for (int i = 0; i < 4; i++) {
                chunks.add(chunkMinForPosition(node[i].min, chunkSize));
            }
            if (chunks.size() == 1)
                return;
        }

        boolean[] isBranch = {
                node[0].Type == Node_Internal,
                node[1].Type == Node_Internal,
                node[2].Type == Node_Internal,
                node[3].Type == Node_Internal,
        };

        if (!isBranch[0] && !isBranch[1] && !isBranch[2] && !isBranch[3]) {
            // To avoid overlap seam mesh with chunk mesh. If all 4 nodes of seam belong to only one chunk, then this is not a seam.
            if(isSeam &&
                    (node[0].getChunk().equals(node[1].getChunk()) &&
                    node[1].getChunk().equals(node[2].getChunk()) &&
                    node[2].getChunk().equals(node[3].getChunk()))
            ){
                return;
            }
            ContourProcessEdge(node, dir, buffer);
        }
        else {
            for (int i = 0; i < 2; i++) {
                OctreeNode[] edgeNodes = new PointerBasedOctreeNode[4];
                int[] c = {
                        edgeProcEdgeMask[dir][i][0],
                        edgeProcEdgeMask[dir][i][1],
                        edgeProcEdgeMask[dir][i][2],
                        edgeProcEdgeMask[dir][i][3],
                };

                for (int j = 0; j < 4; j++) {
                    if (!isBranch[j]) {
                        edgeNodes[j] = node[j];
                    }
                    else {
                        edgeNodes[j] = node[j].children[c[j]];
                    }
                }

                ContourEdgeProc(edgeNodes, edgeProcEdgeMask[dir][i][4], buffer, isSeam, chunkSize);
            }
        }
    }

    private void ContourFaceProc(OctreeNode[] node, int dir, List<Integer> buffer, boolean isSeam, int chunkSize) {
        if (node[0] == null || node[1] == null) {
            return;
        }

        // bit of a hack but it works: prevent overlapping seams by only processing edges that straddle multiple chunks
        if (isSeam && chunkMinForPosition(node[0].min, chunkSize).equals(chunkMinForPosition(node[1].min, chunkSize))) {
            return;
        }

        boolean[] isBranch = {
                node[0].Type == Node_Internal,
                node[1].Type == Node_Internal,
        };

        if (isBranch[0] || isBranch[1]) {
            for (int i = 0; i < 4; i++) {
                OctreeNode[] faceNodes = new PointerBasedOctreeNode[2];
                int[] c = {
                        faceProcFaceMask[dir][i][0], faceProcFaceMask[dir][i][1],
                };

                for (int j = 0; j < 2; j++) {
                    if (!isBranch[j]) {
                        faceNodes[j] = node[j];
                    }
                    else {
                        faceNodes[j] = node[j].children[c[j]];
                    }
                }
                ContourFaceProc(faceNodes, faceProcFaceMask[dir][i][2], buffer, isSeam, chunkSize);
            }

            int[][] orders = {
                    { 0, 0, 1, 1 },
                    { 0, 1, 0, 1 },
            };

            for (int i = 0; i < 4; i++) {
                OctreeNode[] edgeNodes = new PointerBasedOctreeNode[4];
                int[] c = {
                        faceProcEdgeMask[dir][i][1],
                        faceProcEdgeMask[dir][i][2],
                        faceProcEdgeMask[dir][i][3],
                        faceProcEdgeMask[dir][i][4],
                };

                int[] order = orders[faceProcEdgeMask[dir][i][0]];
                for (int j = 0; j < 4; j++) {
                    if (!isBranch[order[j]]) {
                        edgeNodes[j] = node[order[j]];
                    }
                    else {
                        edgeNodes[j] = node[order[j]].children[c[j]];
                    }
                }

                ContourEdgeProc(edgeNodes, faceProcEdgeMask[dir][i][5], buffer, isSeam, chunkSize);
            }
        }
    }

    public void ContourCellProc(OctreeNode node, List<Integer> buffer, boolean isSeam, int chunkSize) {
        if (node == null || node.Type == Node_Leaf) {
            return;
        }

        for (int i = 0; i < 8; i++) {
            ContourCellProc(node.children[i], buffer, isSeam, chunkSize);
        }

        for (int i = 0; i < 12; i++) {
            OctreeNode[] faceNodes = new PointerBasedOctreeNode[2];
            int[] c = { cellProcFaceMask[i][0], cellProcFaceMask[i][1] };

            faceNodes[0] = node.children[c[0]];
            faceNodes[1] = node.children[c[1]];

            ContourFaceProc(faceNodes, cellProcFaceMask[i][2], buffer, isSeam, chunkSize);
        }

        for (int i = 0; i < 6; i++) {
            OctreeNode[] edgeNodes = new PointerBasedOctreeNode[4];
            int[] c = {cellProcEdgeMask[i][0], cellProcEdgeMask[i][1], cellProcEdgeMask[i][2], cellProcEdgeMask[i][3]};

            for (int j = 0; j < 4; j++) {
                edgeNodes[j] = node.children[c[j]];
            }

            ContourEdgeProc(edgeNodes, cellProcEdgeMask[i][4], buffer, isSeam, chunkSize);
        }
    }

    private void GenerateVertexIndices(OctreeNode node, List<MeshVertex> vertexBuffer) {
        if (node == null) {
            return;
        }
        if (node.Type != Node_Leaf) {
            for (int i = 0; i < 8; i++) {
                GenerateVertexIndices(node.children[i], vertexBuffer);
            }
        }
        if (node.Type != Node_Internal) {
            node.index = vertexBuffer.size();
            vertexBuffer.add(createMeshVertex(node));
        }
    }

    private MeshVertex createMeshVertex(OctreeNode node){
        if (node instanceof PointerBasedOctreeNode) {
            return new MeshVertex(((PointerBasedOctreeNode)node).drawInfo.position,
                    ((PointerBasedOctreeNode)node).drawInfo.averageNormal,
                    ((PointerBasedOctreeNode)node).drawInfo.color);
        }
        return null;
    }

    protected void GenerateMeshFromOctree(OctreeNode node, boolean isSeam, MeshBuffer buffer, int chunkSize) {
        if (node == null) {
            return;
        }
        List<MeshVertex> vertices = new ArrayList<>();
        List<Integer> indcies = new ArrayList<>();
        GenerateVertexIndices(node, vertices);
        ContourCellProc(node, indcies, isSeam, chunkSize);

        buffer.setVertices(BufferUtil.createDcFlippedBufferAOS(vertices));
        buffer.setIndicates(BufferUtil.createFlippedBuffer(indcies));
        buffer.setNumVertices(vertices.size());
        buffer.setNumIndicates(indcies.size());
        vertices.clear();
        indcies.clear();
    }

    public final void processNodesToMesh(List<OctreeNode> seamNodes, Vec3i currNodeMin, int rootNodeSize, boolean isSeam, MeshBuffer meshBuffer){
        OctreeNode seamOctreeRoot = constructTreeUpwards(new ArrayList<>(seamNodes), currNodeMin, rootNodeSize);
        GenerateMeshFromOctree(seamOctreeRoot, isSeam, meshBuffer, rootNodeSize);
    }

    protected void inlineInsertionSwap8(int[] data) {
        int i, j;
        for (i = 1; i < 8; i++) {
            int tmp = data[i];
            for (j = i; j >= 1 && tmp < data[j-1]; j--) {
                data[j] = data[j-1];
            }
            data[j] = tmp;
        }
    }

    protected int findDominantMaterial(int[] mv) {
        int MATERIAL_NONE = 200;
        int MATERIAL_AIR  = 201;

        int[] data = { mv[0], mv[1], mv[2], mv[3], mv[4], mv[5], mv[6], mv[7] };
        inlineInsertionSwap8(data);

        int current = data[0];
        int count = 1;
        int maxCount = 0;
        int maxMaterial = 0;

        for (int i = 1; i < 8; i++) {
            int m = data[i];
            if (m == MATERIAL_AIR || m == MATERIAL_NONE) {
                continue;
            }

            if (current != m) {
                if (count > maxCount) {
                    maxCount = count;
                    maxMaterial = current;
                }
                current = m;
                count = 1;
            }
            else {
                count++;
            }
        }

        if (count > maxCount) {
            maxMaterial = current;
        }
        return maxMaterial;
    }

    public Vec4f CalculateSurfaceNormal(Vec4f p) {
//	    float H = 0.001f;
//	    float dx = Density.Density_Func(p.add(new Vec3f(H, 0.f, 0.f)), densityField) - Density.Density_Func(p.sub(new Vec3f(H, 0.f, 0.f)), densityField);
//	    float dy = Density.Density_Func(p.add(new Vec3f(0.f, H, 0.f)), densityField) - Density.Density_Func(p.sub(new Vec3f(0.f, H, 0.f)), densityField);
//	    float dz = Density.Density_Func(p.add(new Vec3f(0.f, 0.f, H)), densityField) - Density.Density_Func(p.sub(new Vec3f(0.f, 0.f, H)), densityField);

        float H = 1f;
        Vec4f xOffcet = new Vec4f(H, 0.f, 0.f, 0.f);
        Vec4f yOffcet = new Vec4f(0.f, H, 0.f, 0.f);
        Vec4f zOffcet = new Vec4f(0.f, 0.f, H, 0.f);
        float dx = getNoise(p.add(xOffcet)) - getNoise(p.sub(xOffcet));
        float dy = getNoise(p.add(yOffcet)) - getNoise(p.sub(yOffcet));
        float dz = getNoise(p.add(zOffcet)) - getNoise(p.sub(zOffcet));

        Vec4f v = new Vec4f(dx, dy, dz);
        v.normalize();
        return v;
    }

    public Vec4f ApproximateZeroCrossingPosition(Vec3f p0, Vec3f p1) {
        float minValue = 100000.f;
        float t = 0.f;
        float currentT = 0.f;
        int steps = 8;  //16;
        float increment = 1.f / steps;
        for (int i = 0; i <= steps; i++) {
            Vec3f p = VoxelHelperUtils.mix(p0, p1, currentT);
            float density = Math.abs(getNoise(p));
            if (density < minValue) {
                minValue = density;
                t = currentT;
            }
            currentT += increment;
        }
        return new Vec4f(VoxelHelperUtils.mix(p0, p1, t), t);
    }

    private int getOctreeSizeByChunkSize(int chunkSize){
        int chunkScaleSize = chunkSize / (meshGen.getVoxelsPerChunk() * meshGen.leafSizeScale);
        return chunkScaleSize * meshGen.leafSizeScale;
    }

    public void computeApplyCSGOperations(CSGOperationInfo lastOperation, ChunkNode node){
    }

    private final Vec3i[] BORDER_EDGE_OFFSETS = {
            new Vec3i(1, 2, 0), new Vec3i(1, 0, 2),
            new Vec3i(2, 1, 0), new Vec3i(0, 1, 2),
            new Vec3i(2, 0, 1), new Vec3i(0, 2, 1),
            new Vec3i(1, 0, 0), new Vec3i(0, 1, 0), new Vec3i(0, 0, 1),
            new Vec3i(1, 2, 2), new Vec3i(2, 2, 1), new Vec3i(2, 1, 2)
    };

    private final Vec3i[] SEAM_NODE_AXIS_OFFSET = {
            new Vec3i(1, 0, 0),
            new Vec3i(0, 1, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(-1, 0, 0),
            new Vec3i(0, -1, 0),
            new Vec3i(0, 0, -1)
    };

    /*
    Algorithm for closing holes in the seams.
    We iterate over each created boundary chunk node. We check each neighbor of the border node -
    do its children contain a surface? If they do, we create a boundary node to close the hole in the seam.
     */
    protected List<OctreeNode> findAndCreateBorderNodes(Set<Integer> seamNodeCodes, Vec3i chunkMin, int chunkSize) {
        Set<OctreeNode> addedNodes = new HashSet<>();
        for (int seamNodeCode : seamNodeCodes) {
            Vec3i seamNodePos = meshGen.positionForCode(seamNodeCode);
            for (int axis = 0; axis < 6; axis++) {
                Vec3i neighbour = seamNodePos.add(SEAM_NODE_AXIS_OFFSET[axis]);
                if((neighbour.x>=0 && neighbour.x < meshGen.getVoxelsPerChunk()) &&
                        (neighbour.y>=0 && neighbour.y < meshGen.getVoxelsPerChunk()) &&
                        (neighbour.z>=0 && neighbour.z < meshGen.getVoxelsPerChunk()))
                {
                    int seamNeighbourCode = meshGen.codeForPosition(neighbour);
                    if (!seamNodeCodes.contains(seamNeighbourCode)) {
                        Vec3i min = neighbour.mul(chunkSize).add(chunkMin);
                        PointerBasedOctreeNode neighbourNode = new PointerBasedOctreeNode(min, chunkSize, OctreeNodeType.Node_Leaf);
                        if(!addedNodes.contains(neighbourNode)){
                            neighbourNode = createBorderNode(neighbourNode);
                            if (neighbourNode!= null) {
                                addedNodes.add(neighbourNode);
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(addedNodes);
    }

    private PointerBasedOctreeNode createBorderNode(PointerBasedOctreeNode node) {
        int corners = 0;
        for (int i = 0; i < 8; i++) {
            Vec3i cornerPos = node.min.add(CHILD_MIN_OFFSETS[i].mul(node.size));
            float density = getNoise(cornerPos);
            int material = density < 0.f ? meshGen.MATERIAL_SOLID : meshGen.MATERIAL_AIR;
            corners |= (material << i);
        }

        for (int i = 0; i < 12; i++) {
            // node size at LOD 0 = 1, LOD 1 = 2, LOD 2 = 4, LOD 3 = 8
            int x = node.min.x + (BORDER_EDGE_OFFSETS[i].x) * node.size / 2;
            int y = node.min.y + (BORDER_EDGE_OFFSETS[i].y) * node.size / 2;
            int z = node.min.z + (BORDER_EDGE_OFFSETS[i].z) * node.size / 2;

            Vec4f nodePos = new Vec4f(x,y,z);
            float density = getNoise(nodePos);
            if ((density < 0 && corners == 0) || (density >= 0 && corners == 255)) {
                OctreeDrawInfo drawInfo = new OctreeDrawInfo();
                drawInfo.position = nodePos.getVec3f();
                drawInfo.color = Constants.Yellow;
                drawInfo.averageNormal = CalculateSurfaceNormal(nodePos).getVec3f();
                node.corners = corners;
                node.drawInfo = drawInfo;
                return node;
            }
        }
        return null;
    }

    private Vec3i getChunkBorder(Vec3i pos){
        Vec3i faces = new Vec3i(0,0,0);
        // checks which side this node is facing
        if (pos.x == 0)
            faces.x = -1;
        else if (pos.x == meshGen.getVoxelsPerChunk()-1)
            faces.x = 1;

        if (pos.y == 0)
            faces.y = -1;
        else if (pos.y == meshGen.getVoxelsPerChunk()-1)
            faces.y = 1;

        if (pos.z == 0)
            faces.z = -1;
        else if (pos.z == meshGen.getVoxelsPerChunk()-1)
            faces.z = 1;
        return faces;
    }

    protected Vec3i tryToCreateBoundSeamPseudoNode(Vec3i chunkMin, int chunkSize, Vec3i pos, int corners) {
        Vec3i chunkBorders = getChunkBorder(pos);
        int nodeMinSize = meshGen.leafSizeScale;
        int leafSize = (chunkSize / meshGen.getVoxelsPerChunk());
        Vec3i leafMin = pos.mul(leafSize).add(chunkMin);

        // if it is facing no border at all or has the highest amount of detail (LOD 0) skip it and drop the node
        if ((chunkBorders.x != 0 || chunkBorders.y != 0 || chunkBorders.z != 0) && leafSize != nodeMinSize) {
            for (int i = 0; i < 12; i++) {
                if (!(  (chunkBorders.x != 0 && chunkBorders.x + 1 == BORDER_EDGE_OFFSETS[i].x) ||
                        (chunkBorders.y != 0 && chunkBorders.y + 1 == BORDER_EDGE_OFFSETS[i].y) ||
                        (chunkBorders.z != 0 && chunkBorders.z + 1 == BORDER_EDGE_OFFSETS[i].z))) {
                    continue;
                }
                // node size at LOD 0 = 1, LOD 1 = 2, LOD 2 = 4, LOD 3 = 8
                Vec3i nodePos = new Vec3i();
                nodePos.x = leafMin.x + (BORDER_EDGE_OFFSETS[i].x) * leafSize / 2;
                nodePos.y = leafMin.y + (BORDER_EDGE_OFFSETS[i].y) * leafSize / 2;
                nodePos.z = leafMin.z + (BORDER_EDGE_OFFSETS[i].z) * leafSize / 2;

                float density = getNoise(nodePos);
                if ((density < 0 && corners == 0) || (density >= 0 && corners == 255)) {
                    return nodePos;
                }
            }
        }
        return null;    // voxel is full inside or outside the volume
    }
}
