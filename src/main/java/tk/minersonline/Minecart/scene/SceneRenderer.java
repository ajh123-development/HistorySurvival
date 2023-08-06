package tk.minersonline.Minecart.scene;

import tk.minersonline.Minecart.glfw.shaders.ShaderModuleData;
import tk.minersonline.Minecart.glfw.shaders.ShaderProgram;

import java.util.*;

import static org.lwjgl.opengl.GL30.*;

public class SceneRenderer {
	private final ShaderProgram shaderProgram;

	public SceneRenderer() {
		List<ShaderModuleData> shaderModuleDataList = new ArrayList<>();
		shaderModuleDataList.add(new ShaderModuleData("shaders/scene.vert", GL_VERTEX_SHADER));
		shaderModuleDataList.add(new ShaderModuleData("shaders/scene.frag", GL_FRAGMENT_SHADER));
		shaderProgram = new ShaderProgram(shaderModuleDataList);
	}

	public void cleanup() {
		shaderProgram.cleanup();
	}

	public void render(Scene scene) {
		shaderProgram.bind();

		scene.getMeshMap().values().forEach(mesh -> {
			glBindVertexArray(mesh.getVaoId());
			glDrawArrays(GL_TRIANGLES, 0, mesh.getNumVertices());
		});

		glBindVertexArray(0);

		shaderProgram.unbind();
	}
}
