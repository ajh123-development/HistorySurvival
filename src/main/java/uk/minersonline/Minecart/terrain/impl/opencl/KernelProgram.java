package uk.minersonline.Minecart.terrain.impl.opencl;

import org.lwjgl.opencl.CLProgram;
import uk.minersonline.Minecart.core.utils.ResourceLoader;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;

public class KernelProgram {
    private final CLProgram kernelProgram;
    private final ComputeContext computeContext;

    public KernelProgram(KernelNames kernelName, ComputeContext computeContext, StringBuilder buildingOptions) {
        this.computeContext = computeContext;
        String programSource = ResourceLoader.loadShader(kernelName.getName());
        this.kernelProgram = CL10.clCreateProgramWithSource(computeContext.getClContext(), programSource, computeContext.getErrcode_ret());
        int errcode = CL10.clBuildProgram(kernelProgram, computeContext.getClDevice(), buildingOptions==null ? "": buildingOptions, null);
        OCLUtils.checkCLError(errcode);
    }

    public CLProgram getKernel() {
        return kernelProgram;
    }

    public void destroyContext(){
        int ret = CL10.clReleaseCommandQueue(computeContext.getClQueue());
        OCLUtils.checkCLError(ret);
        ret = CL10.clReleaseProgram(kernelProgram);
        OCLUtils.checkCLError(ret);
        CL.destroy(); // Not strictly necessary
    }
}
