package uk.minersonline.Minecart.terrain.impl.opencl;

import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLDevice;

import java.nio.IntBuffer;

public class ComputeContext {
    private final CLDevice clDevice;
    private final CLCommandQueue clQueue;
    private final CLContext clContext;
    private final IntBuffer errcode_ret;
    public final int defaultMaterial = 0;

    public ComputeContext(CLDevice clDevice, CLCommandQueue clQueue, CLContext clContext, IntBuffer errcode_ret) {
        this.clDevice = clDevice;
        this.clQueue = clQueue;
        this.clContext = clContext;
        this.errcode_ret = errcode_ret;
    }

    public CLDevice getClDevice() {
        return clDevice;
    }

    public CLCommandQueue getClQueue() {
        return clQueue;
    }

    public CLContext getClContext() {
        return clContext;
    }

    public IntBuffer getErrcode_ret() {
        return errcode_ret;
    }
}
