package uk.minersonline.Minecart.terrain.csg;

import uk.minersonline.Minecart.core.math.Vec3f;
import uk.minersonline.Minecart.core.math.Vec3i;
import uk.minersonline.Minecart.core.math.Vec4f;
import uk.minersonline.Minecart.core.math.Vec4i;
import uk.minersonline.Minecart.terrain.ChunkNode;
import uk.minersonline.Minecart.terrain.VoxelOctree;
import uk.minersonline.Minecart.terrain.entities.CSGOperationInfo;
import uk.minersonline.Minecart.terrain.impl.CPUDensityField;
import uk.minersonline.Minecart.terrain.impl.MeshGenerationContext;
import uk.minersonline.Minecart.terrain.utils.SimplexNoise;
import uk.minersonline.Minecart.terrain.utils.VoxelHelperUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.Math.max;

public class CpuCsgImpl implements ICSGOperations {
    final public static Logger logger = Logger.getLogger(CpuCsgImpl.class.getName());
    private MeshGenerationContext meshGen;
    private final ExecutorService service;
    private final int availableProcessors;
    private final Map<Long, ChunkNode> mortonCodesChunksMap;

    public CpuCsgImpl(Map<Long, ChunkNode> chunks) {
        this.mortonCodesChunksMap = chunks;
        availableProcessors = max(1, Runtime.getRuntime().availableProcessors() / 2);
        service = Executors.newFixedThreadPool(availableProcessors, new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("CpuCsgImpl " + count.getAndIncrement());
                return thread;
            }
        });
    }

    @Override
    public void ApplyReduceOperations(ChunkNode node, CPUDensityField field, Map<Vec4i, CPUDensityField> densityFieldCache) {
        for (int i = 0; i < 8; i++) {
            long locCodeChild = (node.chunkCode<<3)|i;
            ChunkNode child = mortonCodesChunksMap.get(locCodeChild);
            if (child!=null && child.chunkCSGEdited) {
                Vec4i key = new Vec4i(child.min, child.size);
                CPUDensityField srcField = densityFieldCache.get(key);
                if (srcField != null) {
                    reduceMultiThread(i, srcField, field);
                    node.chunkCSGEdited = true;
                }
            }
        }
    }

    private void reduce(int chunkOrder, CPUDensityField srcField, CPUDensityField dstField){
        int size = meshGen.getHermiteIndexSize();
        Vec3i dstOffset = meshGen.offset(chunkOrder, size/2);
        for(int srcCellZ = 0; srcCellZ < size; srcCellZ += 2 ) {
            for (int srcCellY = 0; srcCellY < size; srcCellY += 2) {
                for (int srcCellX = 0; srcCellX < size; srcCellX += 2) {
                    Vec3i dstCellOffset = new Vec3i(srcCellX / 2, srcCellY / 2, srcCellZ / 2).add(dstOffset);
                    reduceCell(srcField, dstField, dstCellOffset, srcCellZ, srcCellY, srcCellX);
                }
            }
        }
    }

    private void reduceMultiThread(int chunkOrder, CPUDensityField srcField, CPUDensityField dstField){
        int size = meshGen.getHermiteIndexSize();
        Vec3i dstOffset = meshGen.offset(chunkOrder, size/2);
        int threadBound = size / availableProcessors;
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < availableProcessors; i++) {
            int from = i * threadBound;
            int to = (i == availableProcessors - 1) ? size : from + threadBound;
            tasks.add(() -> {
                for (int z = from + from % 2; z < to; z +=2) {
                    for (int y = 0; y < size; y += 2) {
                        for (int x = 0; x < size; x += 2) {
                            Vec3i dstCellOffset = new Vec3i(x / 2, y / 2, z / 2).add(dstOffset);
                            reduceCell(srcField, dstField, dstCellOffset, z, y, x);
                        }
                    }
                }
                return 1;
            });
        }
        VoxelOctree.performIntCallableTask(tasks, service, logger);
    }

    private void reduceCell(CPUDensityField srcField, CPUDensityField dstField, Vec3i dstCellOffset, int srcCellZ, int srcCellY, int srcCellX) {
        int startpoint_material = srcField.materials[meshGen.getMaterialIndex(srcCellX, srcCellY, srcCellZ)];
        int NUM_AXES = 3;
        for(int axis = 0; axis < NUM_AXES; axis++) {
            int numIntersections = 0;
            int[] srcEndPoint = new int[]{srcCellX, srcCellY, srcCellZ};
            srcEndPoint[axis] += 2;

            Vec4f destNorm = new Vec4f();
            if(srcEndPoint[0] < meshGen.getFieldSize() && srcEndPoint[1] < meshGen.getFieldSize() && srcEndPoint[2] < meshGen.getFieldSize()) {
                int[] srcMidPoint = new int[]{srcCellX, srcCellY, srcCellZ};
                srcMidPoint[axis]++;
                int midpoint_material = srcField.materials[meshGen.getMaterialIndex(srcMidPoint[0], srcMidPoint[1], srcMidPoint[2])];
                Vec4f startPointNorm = srcField.hermiteEdges.get(meshGen.getEdgeCodeByPos(srcCellX, srcCellY, srcCellZ, axis));
                if (startPointNorm!=null && startpoint_material != midpoint_material) {
                    destNorm = destNorm.add(startPointNorm.getVec3f(), startPointNorm.w * 0.5f);
                    numIntersections++;
                }

                int endpoint_material = srcField.materials[meshGen.getMaterialIndex(srcEndPoint[0], srcEndPoint[1], srcEndPoint[2])];
                if (midpoint_material != endpoint_material) {
                    Vec4f srcMidPointNorm = srcField.hermiteEdges.get(meshGen.getEdgeCodeByPos(srcMidPoint[0], srcMidPoint[1], srcMidPoint[2], axis));
                    if (srcMidPointNorm != null) {
                        destNorm = destNorm.add(srcMidPointNorm.getVec3f(), 0.5f + srcMidPointNorm.w * 0.5f);
                        numIntersections++;
                    }
                }
            }

            if(numIntersections>0) {
                float invNum = 1.0f / numIntersections;
                Vec3f d = destNorm.getVec3f().mul(invNum).normalize();
                float w = destNorm.w * invNum;
                int destEdgeCode = meshGen.getEdgeCodeByPos(dstCellOffset.x, dstCellOffset.y, dstCellOffset.z, axis);
                dstField.hermiteEdges.put(destEdgeCode, new Vec4f(d, w));
            }
        }
        int dstMaterialIndex = meshGen.getMaterialIndex(dstCellOffset.x, dstCellOffset.y, dstCellOffset.z);
        dstField.materials[dstMaterialIndex] = startpoint_material; // save changed material for use when next lod level reduce
    }

    @Override
    public boolean ApplyCSGOperations(MeshGenerationContext meshGen, CSGOperationInfo lastOperation, ChunkNode node, CPUDensityField field){
        this.meshGen = meshGen;

        Vec4i fieldOffset = LeafScaleVec(node.min);
        int sampleScale = node.size / (meshGen.leafSizeScale * meshGen.getVoxelsPerChunk());
        int fieldBufferSize = meshGen.fieldSize * meshGen.fieldSize * meshGen.fieldSize;
        int[] d_updatedIndices = new int[fieldBufferSize];
        Vec3i[] d_updatedPoints = new Vec3i[fieldBufferSize];

        int numUpdatedPoints = CSG_HermiteIndicesMultiThread(fieldOffset, lastOperation, sampleScale, field.materials,
                d_updatedIndices, d_updatedPoints);
        if (numUpdatedPoints <= 0) {    // < 0 will be an error code
            return false;
        }

        Vec3i[] d_compactUpdatedPoints = new Vec3i[numUpdatedPoints];
        compactElements(d_updatedIndices, d_updatedPoints, d_compactUpdatedPoints);

        int[] d_generatedEdgeIndices = new int [numUpdatedPoints * 6];
        int numCompactEdgeIndices = FindUpdatedEdgesMultiThread(d_compactUpdatedPoints, d_generatedEdgeIndices);

        Set<Integer> d_invalidatedEdges = CompactIndexArray(d_generatedEdgeIndices, numCompactEdgeIndices);
        Map<Integer, Vec4f> createdEdgesHermiteData = findEdgeIntersections(fieldOffset, lastOperation, sampleScale, d_invalidatedEdges, field.materials);

        if (d_invalidatedEdges.size() > 0 && field.hermiteEdges.size() > 0) {
            field.hermiteEdges.keySet().removeAll(d_invalidatedEdges);
        }

        if (createdEdgesHermiteData.size() > 0) {
            if (field.hermiteEdges.size() > 0) {
                field.hermiteEdges.putAll(createdEdgesHermiteData);
            } else {
                field.hermiteEdges = createdEdgesHermiteData;
            }
        }
        return true;
    }

    private int CSG_HermiteIndicesMultiThread(Vec4i worldspaceOffset, CSGOperationInfo lastOperation, int sampleScale, int[] field_materials,
                                   int[] updated_indices, Vec3i[] updated_positions){
        int bound = meshGen.getFieldSize();
        int threadBound = (bound * bound * bound) / availableProcessors;
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < availableProcessors; i++) {
            int finalI = i;
            tasks.add(() -> {
                int from = finalI * threadBound;
                int to = from + threadBound;
                int size = 0;
                for (int it = from; it < to; it++) {
                    int x = it % bound;
                    int y = (it / bound) % bound;
                    int z = (it / bound / bound);
                    size = processMaterials(worldspaceOffset, lastOperation, sampleScale, field_materials, updated_indices, updated_positions, size, z, y, x);
                }
                return size;
            });
        }
        return VoxelOctree.performIntCallableTask(tasks, service, logger);
    }

    private int processMaterials(Vec4i worldspaceOffset, CSGOperationInfo lastOperation, int sampleScale, int[] field_materials, int[] updated_indices, Vec3i[] updated_positions, int size, int z, int y, int x) {
        Vec3i local_pos = new Vec3i(x, y, z);
        int sx = sampleScale * x;
        int sy = sampleScale * y;
        int sz = sampleScale * z;

        int index = meshGen.getMaterialIndex(local_pos);
        int oldMaterial = field_materials[index];
        int material = field_materials[index];

        Vec3f world_pos = new Vec3f(worldspaceOffset.x + sx, worldspaceOffset.y + sy, worldspaceOffset.z + sz);
        material = BrushMaterial(world_pos, lastOperation, material);

        int updated = material != oldMaterial ? 1 : 0;
        if(updated==1){
            field_materials[index] = material;
            size++;
        }
        updated_indices[index] = updated;
        updated_positions[index] = local_pos;
        return size;
    }

    private void compactElements(int[] edgeValid,  Vec3i[] edgeNormals,
                                 Vec3i[] compactNormals) {
        int cid = 0;
        for (int index = 0; index<edgeValid.length; index++) {
            if (edgeValid[index]==1) {
                compactNormals[cid++] = edgeNormals[index];
            }
        }
    }

    private int FindUpdatedEdgesMultiThread(Vec3i[] updatedHermiteIndices,
                                 int[] updatedHermiteEdgeIndices) {
        int bound = updatedHermiteIndices.length;
        final int threadBound = bound / availableProcessors;
        List<Callable<Integer>> tasks = new ArrayList<>();

        for (int i = 0; i < availableProcessors; i++) {
            int from = i * threadBound;
            int to = from + threadBound;
            boolean last = (i == availableProcessors - 1 && to <= bound - 1);
            tasks.add(() -> FindUpdatedEdges(from, last ? bound : to, updatedHermiteIndices, updatedHermiteEdgeIndices));
        }
        return VoxelOctree.performIntCallableTask(tasks, service, logger);
    }

    private int FindUpdatedEdges(int from, int to, Vec3i[] updatedHermiteIndices,
            int[] updatedHermiteEdgeIndices)
    {
        int size=0;
        for(int id=from; id<to; id++){
            int edgeIndex = id * 6;
            Vec3i pos = updatedHermiteIndices[id];
            int posIndex = (pos.x | (pos.y << meshGen.getIndexShift()) | (pos.z << (meshGen.getIndexShift() * 2))) << 2;

            updatedHermiteEdgeIndices[edgeIndex + 0] = posIndex | 0;
            updatedHermiteEdgeIndices[edgeIndex + 1] = posIndex | 1;
            updatedHermiteEdgeIndices[edgeIndex + 2] = posIndex | 2;
            size+=3;

            if (pos.x > 0) {
		        Vec3i xPos = pos.sub(new Vec3i(1, 0, 0));
		        int xPosIndex = (xPos.x | (xPos.y << meshGen.getIndexShift()) | (xPos.z << (meshGen.getIndexShift() * 2))) << 2;
                updatedHermiteEdgeIndices[edgeIndex + 3] = xPosIndex | 0;
                size++;
            }
            else {
                updatedHermiteEdgeIndices[edgeIndex + 3] = -1;
            }

            if (pos.y > 0) {
                Vec3i yPos = pos.sub(new Vec3i(0, 1, 0));
		        int yPosIndex = (yPos.x | (yPos.y << meshGen.getIndexShift()) | (yPos.z << (meshGen.getIndexShift() * 2))) << 2;
                updatedHermiteEdgeIndices[edgeIndex + 4] = yPosIndex | 1;
                size++;
            }
            else {
                updatedHermiteEdgeIndices[edgeIndex + 4] = -1;
            }

            if (pos.z > 0) {
                Vec3i zPos = pos.sub(new Vec3i(0, 0, 1));
		        int zPosIndex = (zPos.x | (zPos.y << meshGen.getIndexShift()) | (zPos.z << (meshGen.getIndexShift() * 2))) << 2;
                updatedHermiteEdgeIndices[edgeIndex + 5] = zPosIndex | 2;
                size++;
            }
            else {
                updatedHermiteEdgeIndices[edgeIndex + 5] = -1;
            }
        }
        return size;
    }

    private Set<Integer> CompactIndexArray(int[] edgeIndices, int numCompactEdgeIndices) {
        int[] compactIndices = new int[numCompactEdgeIndices];
        int current = 0;
        for (int edgeIndex : edgeIndices) {
            if (edgeIndex != -1) {
                compactIndices[current++] = edgeIndex;
            }
        }
        return Arrays.stream(compactIndices).boxed().collect(Collectors.toSet());
    }

    private Map<Integer, Vec4f> findEdgeIntersections(Vec4i offset, CSGOperationInfo lastOperation, int sampleScale,
                                                      Set<Integer> generatedHermiteEdgeIndices, int[] materials) {
        Map<Integer, Vec4f> hermiteData = new HashMap<>();
        int FIELD_BUFFER_SIZE = meshGen.getFieldSize() * meshGen.getFieldSize() * meshGen.getFieldSize();
        for (int generatedEdgeIndex : generatedHermiteEdgeIndices) {
            int edgeNumber = generatedEdgeIndex & 3;
            int edgeIndex = generatedEdgeIndex >> 2;
            Vec3i position = new Vec4i(
                    (edgeIndex >> (meshGen.getIndexShift() * 0)) & meshGen.getIndexMask(),
                    (edgeIndex >> (meshGen.getIndexShift() * 1)) & meshGen.getIndexMask(),
                    (edgeIndex >> (meshGen.getIndexShift() * 2)) & meshGen.getIndexMask(),
                    0);

            int materialIndex0 = meshGen.getMaterialIndex(position);
            int materialIndex1 = meshGen.getMaterialIndex(position.add(VoxelOctree.EDGE_END_OFFSETS[edgeNumber]));

            // There should be no need to check these indices, the previous call to
            // RemoveInvalidIndices should have validated the generatedEdgeIndices array
            int material0 = materialIndex0 < FIELD_BUFFER_SIZE ? materials[materialIndex0] : meshGen.MATERIAL_AIR;
	        int material1 = materialIndex1 < FIELD_BUFFER_SIZE ? materials[materialIndex1] : meshGen.MATERIAL_AIR;

            int signChange = (material0 == meshGen.MATERIAL_AIR && material1 != meshGen.MATERIAL_AIR) ||
                    (material1 == meshGen.MATERIAL_AIR && material0 != meshGen.MATERIAL_AIR) ? 1 : 0;

            int edgeValid = signChange==1 && generatedEdgeIndex != -1 ? 1 : 0;
            if (edgeValid == 1) {
                hermiteData.put(generatedEdgeIndex, calculateNorm(lastOperation, offset, sampleScale, edgeNumber * 4, position));
            }
        }
        return hermiteData;
    }

    private Vec4f calculateNorm(CSGOperationInfo lastOperation, Vec4i offset, int sampleScale, int edgeIndex, Vec3i local_pos){
        int e0 = VoxelOctree.edgevmap[edgeIndex][0];
        int e1 = VoxelOctree.edgevmap[edgeIndex][1];

        Vec3i world_pos = (local_pos.mul(sampleScale)).add(offset);
        Vec4f p0 = world_pos.add(VoxelOctree.CHILD_MIN_OFFSETS[e0]).toVec4f();
        Vec4f p1 = world_pos.add(VoxelOctree.CHILD_MIN_OFFSETS[e1].mul(sampleScale)).toVec4f();

        float t = BrushZeroCrossing(p0, p1, lastOperation);
        Vec3f p = VoxelHelperUtils.mix(p0, p1, t).getVec3f();

        Vec3f n = BrushNormal(p, lastOperation);
        return new Vec4f(n, t);
    }

    private Vec4i LeafScaleVec(Vec3i v) {
        Vec4i s = new Vec4i();
        s.x = v.x / meshGen.leafSizeScale;
        s.y = v.y / meshGen.leafSizeScale;
        s.z = v.z / meshGen.leafSizeScale;
        s.w = 0;
        return s;
    }

    float BrushDensity(Vec3f worldspaceOffset, CSGOperationInfo lastOperation) {
        float [] brushDensity = {Float.MIN_VALUE, Float.MAX_VALUE};
        //brushDensity[0] = Density_Cuboid(worldspaceOffset, op.getOrigin(), op.getDimensions(), op.getRotateY());
        brushDensity[0] = SimplexNoise.Density_Cuboid(worldspaceOffset, lastOperation.getOrigin().getVec3f(), lastOperation.getDimensions().getVec3f());
        brushDensity[1] = SimplexNoise.Density_Sphere(worldspaceOffset, lastOperation.getOrigin().getVec3f(), lastOperation.getDimensions().x);
        return brushDensity[lastOperation.getBrushShape().ordinal()];
    }

    // "concatenate" the brush operations to get the final density for the brush in isolation
    private float BrushZeroCrossing(Vec4f p0, Vec4f p1, CSGOperationInfo lastOperation) {
        float minDensity = Float.MAX_VALUE;
        float crossing = 0.f;
        for (float t = 0.f; t <= 1.f; t += (1.f/16.f)) {
		    Vec3f p = VoxelHelperUtils.mix(p0, p1, t).getVec3f();
            float d = Math.abs(BrushDensity(p, lastOperation));
            if (d < minDensity) {
                crossing = t;
                minDensity = d;
            }
        }
        return crossing;
    }

    private int BrushMaterial(Vec3f world_pos, CSGOperationInfo lastOperation, int material) {
        int m = material;
        int[] operationMaterial = {lastOperation.getMaterial(), meshGen.MATERIAL_AIR};
        float d = BrushDensity(world_pos, lastOperation);
        if (d <= 0.f) {
            m = operationMaterial[lastOperation.getType()];
        }
        return m;
    }

    private Vec3f BrushNormal(Vec3f p, CSGOperationInfo lastOperation) {
		    //float d = BrushDensity(p, op);
            //if (d > 0.f) continue;

		    float h = 0.001f;
            Vec3f xOffcet = new Vec3f(h, 0.f, 0.f);
            Vec3f yOffcet = new Vec3f(0.f, h, 0.f);
            Vec3f zOffcet = new Vec3f(0.f, 0.f, h);
		    float dx0 = BrushDensity(p.add(xOffcet), lastOperation);
		    float dx1 = BrushDensity(p.sub(xOffcet), lastOperation);

		    float dy0 = BrushDensity(p.add(yOffcet), lastOperation);
		    float dy1 = BrushDensity(p.sub(yOffcet), lastOperation);

		    float dz0 = BrushDensity(p.add(zOffcet), lastOperation);
		    float dz1 = BrushDensity(p.sub(zOffcet), lastOperation);

            float flip = lastOperation.getType() == 0 ? 1.f : -1.f;
            Vec3f normal = new Vec3f(dx0 - dx1, dy0 - dy1, dz0 - dz1).normalize().mul(flip);

        return normal;
    }
}