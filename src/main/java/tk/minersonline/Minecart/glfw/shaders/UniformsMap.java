package tk.minersonline.Minecart.glfw.shaders;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.util.*;

import static org.lwjgl.opengl.GL20.*;

public class UniformsMap {

	private final int programId;
	private final Map<String, Integer> uniforms;

	public UniformsMap(int programId) {
		this.programId = programId;
		uniforms = new HashMap<>();
	}

	public void createUniform(String uniformName) {
		int uniformLocation = glGetUniformLocation(programId, uniformName);
		if (uniformLocation < 0) {
			throw new RuntimeException("Could not find uniform [" + uniformName + "] in shader program [" +
					programId + "]");
		}
		uniforms.put(uniformName, uniformLocation);
	}

	public void setUniform(String uniformName, Matrix4f value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			Integer location = uniforms.get(uniformName);
			if (location == null) {
				throw new RuntimeException("Could not find uniform [" + uniformName + "]");
			}
			glUniformMatrix4fv(location, false, value.get(stack.mallocFloat(16)));
		}
	}
}
