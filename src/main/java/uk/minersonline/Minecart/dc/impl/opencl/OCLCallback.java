package uk.minersonline.Minecart.dc.impl.opencl;

import org.lwjgl.opencl.CLContextCallback;

import java.nio.ByteBuffer;

public class OCLCallback extends CLContextCallback {
	@Override
	protected void handleMessage(String errinfo, ByteBuffer private_info) {
		System.err.println("OpenCL Context Error:");
		System.err.println(errinfo);
	}
}