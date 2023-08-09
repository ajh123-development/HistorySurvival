package tk.minersonline.Minecart.scene;

import tk.minersonline.Minecart.glfw.shaders.ShaderModuleData;
import tk.minersonline.Minecart.glfw.shaders.ShaderProgram;
import tk.minersonline.Minecart.glfw.shaders.UniformsMap;
import tk.minersonline.Minecart.scene.objects.*;

import java.util.*;

import static org.lwjgl.opengl.GL30.*;

public class SceneRenderer {
	private final ShaderProgram shaderProgram;
	private UniformsMap uniformsMap;

	public SceneRenderer() {
		List<ShaderModuleData> shaderModuleDataList = new ArrayList<>();
		shaderModuleDataList.add(new ShaderModuleData("shaders/scene/shader.vert", GL_VERTEX_SHADER));
		shaderModuleDataList.add(new ShaderModuleData("shaders/scene/shader.frag", GL_FRAGMENT_SHADER));
		shaderProgram = new ShaderProgram(shaderModuleDataList);
		createUniforms();
	}

	public void cleanup() {
		shaderProgram.cleanup();
	}

	private void createUniforms() {
		uniformsMap = new UniformsMap(shaderProgram.getProgramId());
		uniformsMap.createUniform("projectionMatrix");
		uniformsMap.createUniform("viewMatrix");
		uniformsMap.createUniform("modelMatrix");
		uniformsMap.createUniform("txtSampler");
	}

	public void render(Scene scene) {
		glDisable(GL_CULL_FACE);
		shaderProgram.bind();

		uniformsMap.setUniform("projectionMatrix", scene.getProjection().getMatrix());
		uniformsMap.setUniform("viewMatrix", scene.getCamera().getViewMatrix());
		uniformsMap.setUniform("txtSampler", 0);

		Collection<Model> models = scene.getModelMap().values();
		TextureCache textureCache = scene.getTextureCache();
		for (Model model : models) {
			List<Entity> entities = model.getEntitiesList();

			for (Material material : model.getMaterialList()) {
				Texture texture = textureCache.getTexture(material.getTexturePath());
				glActiveTexture(GL_TEXTURE0);
				texture.bind();

				for (Mesh mesh : material.getMeshList()) {
					glBindVertexArray(mesh.getVaoId());
					for (Entity entity : entities) {
						uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
						glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
					}
				}
			}
		}

		glBindVertexArray(0);

		shaderProgram.unbind();
		glEnable(GL_CULL_FACE);
	}
}
