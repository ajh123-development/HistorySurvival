package uk.minersonline.Minecart.terrain.impl.opencl;

import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLMem;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class BufferGpuService {
    private final ArrayList<BufferGpu> holder;
    private final ComputeContext ctx;

    public BufferGpuService(ComputeContext ctx) {
        this.ctx = ctx;
        this.holder = new ArrayList<>();
    }

    public BufferGpu create(String name, int size, MemAccess access){
        long flags = OCLUtils.getMemoryAccessFlags(access);
        CLMem mem = CL10.clCreateBuffer(ctx.getClContext(), flags, size, ctx.getErrcode_ret());
        OCLUtils.checkCLError(ctx.getErrcode_ret());
        BufferGpu bufferGpu = new BufferGpu(name, mem);
        holder.add(bufferGpu);
        return bufferGpu;
    }

    public BufferGpu create(String name, int size, long flags){
        CLMem mem = CL10.clCreateBuffer(ctx.getClContext(), flags, size, ctx.getErrcode_ret());
        OCLUtils.checkCLError(ctx.getErrcode_ret());
        BufferGpu bufferGpu = new BufferGpu(name, mem);
        holder.add(bufferGpu);
        return bufferGpu;
    }

    public BufferGpu create(String name, Buffer buf, long flags){
        CLMem mem;
        if (buf instanceof IntBuffer){
            mem = CL10.clCreateBuffer(ctx.getClContext(), flags, (IntBuffer) buf, ctx.getErrcode_ret());
        } else if(buf instanceof FloatBuffer){
            mem = CL10.clCreateBuffer(ctx.getClContext(), flags, (FloatBuffer) buf, ctx.getErrcode_ret());
        } else {
            mem = CL10.clCreateBuffer(ctx.getClContext(), flags, (ByteBuffer) buf, ctx.getErrcode_ret());
        }
        OCLUtils.checkCLError(ctx.getErrcode_ret());
        BufferGpu bufferGpu = new BufferGpu(name, mem);
        holder.add(bufferGpu);
        return bufferGpu;
    }

    public void releaseAll(){
        for(BufferGpu buf : holder){
            if(buf.getMem()!=null){
                int err = CL10.clReleaseMemObject(buf.getMem());
                OCLUtils.checkCLError(err);
                buf.setMem(null);
            }
        }
        holder.clear();
        CL10.clFinish(ctx.getClQueue());
    }

    public void release(BufferGpu buf){
        if(buf.getMem()!=null) {
            int err = CL10.clReleaseMemObject(buf.getMem());
            OCLUtils.checkCLError(err);
            buf.setMem(null);
        }
        holder.removeIf(item -> item.getName().equals(buf.getName()));
    }
}
