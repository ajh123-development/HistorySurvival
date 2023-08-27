package uk.minersonline.Minecart.dc.csg;

import uk.minersonline.Minecart.core.math.Vec4i;
import uk.minersonline.Minecart.dc.ChunkNode;
import uk.minersonline.Minecart.dc.entities.CSGOperationInfo;
import uk.minersonline.Minecart.dc.impl.CPUDensityField;
import uk.minersonline.Minecart.dc.impl.MeshGenerationContext;

import java.util.Map;

public interface ICSGOperations {
    boolean ApplyCSGOperations(MeshGenerationContext meshGen, CSGOperationInfo opInfo, ChunkNode node, CPUDensityField field);
    void ApplyReduceOperations(ChunkNode node, CPUDensityField field, Map<Vec4i, CPUDensityField> densityFieldCache);
}
