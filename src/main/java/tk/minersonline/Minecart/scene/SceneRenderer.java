package tk.minersonline.Minecart.scene;

import tk.minersonline.Minecart.glfw.shaders.ShaderModuleData;
import tk.minersonline.Minecart.glfw.shaders.ShaderProgram;
import tk.minersonline.Minecart.glfw.shaders.UniformsMap;
import tk.minersonline.Minecart.scene.objects.Entity;
import tk.minersonline.Minecart.scene.objects.Model;

import java.util.*;

import static org.lwjgl.opengl.GL30.*;

public class SceneRenderer {
	private final ShaderProgram shaderProgram;
	private UniformsMap uniformsMap;

	public SceneRenderer() {
		List<ShaderModuleData> shaderModuleDataList = new ArrayList<>();
		shaderModuleDataList.add(new ShaderModuleData("shaders/scene.vert", GL_VERTEX_SHADER));
		shaderModuleDataList.add(new ShaderModuleData("shaders/scene.frag", GL_FRAGMENT_SHADER));
		shaderProgram = new ShaderProgram(shaderModuleDataList);
		createUniforms();
	}

	public void cleanup() {
		shaderProgram.cleanup();
	}

	private void createUniforms() {
		uniformsMap = new UniformsMap(shaderProgram.getProgramId());
		uniformsMap.createUniform("projectionMatrix");
		uniformsMap.createUniform("modelMatrix");
	}

	public void render(Scene scene) {
		shaderProgram.bind();

		uniformsMap.setUniform("projectionMatrix", scene.getProjection().getMatrix());

		Collection<Model> models = scene.getModelMap().values();
		for (Model model : models) {
			model.getMeshList().stream().forEach(mesh -> {
				glBindVertexArray(mesh.getVaoId());
				List<Entity> entities = model.getEntitiesList();
				for (Entity entity : entities) {
					uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
					glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
				}
			});
		}

		glBindVertexArray(0);

		shaderProgram.unbind();
	}
}
