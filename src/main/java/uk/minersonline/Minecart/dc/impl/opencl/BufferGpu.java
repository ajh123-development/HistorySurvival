package uk.minersonline.Minecart.dc.impl.opencl;

import org.lwjgl.opencl.CLMem;

public class BufferGpu {
    private CLMem mem;
    private final String name;

    public String getName() {
        return name;
    }

    public BufferGpu(String name, CLMem mem) {
        this.mem = mem;
        this.name = name;
    }

    public CLMem getMem() {
        return mem;
    }

    public void setMem(CLMem mem) {
        this.mem = mem;
    }
}
