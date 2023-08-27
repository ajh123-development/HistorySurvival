package uk.minersonline.Minecart.dc.impl.opencl;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL12;
import org.lwjgl.opencl.CLProgram;
import uk.minersonline.Minecart.core.utils.ResourceLoader;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.system.MemoryUtil.NULL;
import static uk.minersonline.Minecart.core.utils.ImageLoader.ioResourceToByteBuffer;

public class KernelsHolder {
    Map<String, CLProgram> kernels = new HashMap<>();
    private final ComputeContext computeContext;

    public KernelsHolder(ComputeContext computeContext) {
        this.computeContext = computeContext;
    }

    public void buildKernel(KernelNames kernelName, StringBuilder buildingOptions){
        PointerBuffer strings = BufferUtils.createPointerBuffer(1);
        PointerBuffer lengths = BufferUtils.createPointerBuffer(1);

        ByteBuffer source;
        try {
            source = ioResourceToByteBuffer("./res/"+kernelName.getName(), 4096);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        strings.put(0, source);
        lengths.put(0, source.remaining());

        CLProgram kernelProgram = CL10.clCreateProgramWithSource(computeContext.getClContext(), source, lengths, computeContext.getErrcode_ret());
        int errcode = CL10.clBuildProgram(kernelProgram, computeContext.getClDevice(), buildingOptions==null ? "": buildingOptions, null);
        OCLUtils.checkCLError(errcode);
        kernels.put(kernelName.name(), kernelProgram);
    }

    public void buildKernel(KernelNames kernelName, StringBuilder buildingOptions, List<String> headers){
        StringBuilder programSource = new StringBuilder();
        for(String header: headers){
            programSource.append(" \n ").append(ResourceLoader.loadShader(header));
        }
        programSource.append(ResourceLoader.loadShader(kernelName.getName()));
        CLProgram kernelProgram = CL10.clCreateProgramWithSource(computeContext.getClContext(), programSource, computeContext.getErrcode_ret());
        int errcode = CL10.clBuildProgram(kernelProgram, computeContext.getClDevice(), buildingOptions==null ? "": buildingOptions, null);
        OCLUtils.checkCLError(errcode);
        kernels.put(kernelName.name(), kernelProgram);
    }

    public CLProgram getKernel(KernelNames kernelName){
        return kernels.get(kernelName.name());
    }

    public void destroyContext(){
        int ret;
        ret = CL10.clReleaseCommandQueue(computeContext.getClQueue());
        OCLUtils.checkCLError(ret);
        for (Map.Entry<String, CLProgram> item : kernels.entrySet()){
            ret = CL10.clReleaseProgram(item.getValue());
            OCLUtils.checkCLError(ret);
        }
        CL.destroy(); // Not strictly necessary
    }
}
