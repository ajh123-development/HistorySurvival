package uk.minersonline.Minecart.terrain.csg;

import uk.minersonline.Minecart.core.math.Vec4i;
import uk.minersonline.Minecart.terrain.ChunkNode;
import uk.minersonline.Minecart.terrain.entities.CSGOperationInfo;
import uk.minersonline.Minecart.terrain.impl.CPUDensityField;
import uk.minersonline.Minecart.terrain.impl.MeshGenerationContext;

import java.util.Map;

public interface ICSGOperations {
    boolean ApplyCSGOperations(MeshGenerationContext meshGen, CSGOperationInfo opInfo, ChunkNode node, CPUDensityField field);
    void ApplyReduceOperations(ChunkNode node, CPUDensityField field, Map<Vec4i, CPUDensityField> densityFieldCache);
}
