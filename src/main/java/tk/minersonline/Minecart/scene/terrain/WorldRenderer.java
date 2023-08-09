package tk.minersonline.Minecart.scene.terrain;

import tk.minersonline.Minecart.glfw.shaders.ShaderModuleData;
import tk.minersonline.Minecart.glfw.shaders.ShaderProgram;
import tk.minersonline.Minecart.glfw.shaders.UniformsMap;
import tk.minersonline.Minecart.scene.Scene;
import tk.minersonline.Minecart.scene.TextureCache;
import tk.minersonline.Minecart.scene.objects.Mesh;
import tk.minersonline.Minecart.scene.objects.Texture;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class WorldRenderer {
	public static boolean FILL_POLYGON = true;
	private final ShaderProgram shaderProgram;
	private UniformsMap uniformsMap;

	public WorldRenderer() {
		List<ShaderModuleData> shaderModuleDataList = new ArrayList<>();
		shaderModuleDataList.add(new ShaderModuleData("shaders/world/shader.vert", GL_VERTEX_SHADER));
		shaderModuleDataList.add(new ShaderModuleData("shaders/world/shader.frag", GL_FRAGMENT_SHADER));
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

	public void render(World world) {
		// Enable face culling
//		glEnable(GL_CULL_FACE);
		// Specify which faces to cull
//		glCullFace(GL_FRONT);
		// Specify the winding order of front faces (usually counter-clockwise)
//		glFrontFace(GL_CCW);
		if (!FILL_POLYGON) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		}

//		shaderProgram.bind();
//
//		uniformsMap.setUniform("projectionMatrix", scene.getProjection().getMatrix());
//		uniformsMap.setUniform("viewMatrix", scene.getCamera().getViewMatrix());
//		uniformsMap.setUniform("txtSampler", 0);
//		uniformsMap.setUniform("modelMatrix", chunk.getModelMatrix());
//		TextureCache textureCache = scene.getTextureCache();
//
//		// Get the mesh data from the chunk
//		Mesh meshData = chunk.getMeshData();
//
//		// Bind the VAO and VBOs
//		glBindVertexArray(meshData.getVaoId());
//
//		Texture texture = textureCache.getTexture("textures/cube.png");
//		glActiveTexture(GL_TEXTURE0);
//		texture.bind();
//
//		// Draw the chunk
//		glDrawElements(GL_TRIANGLES, meshData.getNumVertices(), GL_UNSIGNED_INT, 0);
//
//		// Unbind the VAO, VBOs, and shader
//		glBindVertexArray(0);
//
//		shaderProgram.unbind();
//		glDisable(GL_CULL_FACE);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}
}
