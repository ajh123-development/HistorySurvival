package uk.minersonline.Minecart.dc.impl;

import uk.minersonline.Minecart.core.math.Vec3i;
import uk.minersonline.Minecart.core.math.Vec4f;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class CPUDensityField {
    public Vec3i min;
    public int size;
    public int[] materials;
    public Map<Integer, Vec4f> hermiteEdges = new ConcurrentSkipListMap<>();
}
