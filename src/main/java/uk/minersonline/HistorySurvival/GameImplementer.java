package uk.minersonline.HistorySurvival;

import org.joml.Vector2f;
import uk.minersonline.Minecart.MinecartGame;
import uk.minersonline.Minecart.glfw.Renderer;
import uk.minersonline.Minecart.glfw.window.Window;
import uk.minersonline.Minecart.glfw.window.listener.KeyListener;
import uk.minersonline.Minecart.glfw.window.listener.MouseListener;
import uk.minersonline.Minecart.scene.Camera;
import uk.minersonline.Minecart.scene.Scene;
import tk.minersonline.Minecart.scene.objects.*;
import uk.minersonline.Minecart.scene.objects.*;
import uk.minersonline.Minecart.scene.terrain.Chunk;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;

public class GameImplementer implements MinecartGame {
	private static final float MOUSE_SENSITIVITY = 0.1f;
	private static final float MOVEMENT_SPEED = 0.5f;

	private Entity cubeEntity;
	private float rotation;

	@Override
	public void cleanup() {

	}

	@Override
	public void init(Window window, Scene scene, Renderer render) {
		float[] positions = new float[]{
				// V0
				-0.5f, 0.5f, 0.5f,
				// V1
				-0.5f, -0.5f, 0.5f,
				// V2
				0.5f, -0.5f, 0.5f,
				// V3
				0.5f, 0.5f, 0.5f,
				// V4
				-0.5f, 0.5f, -0.5f,
				// V5
				0.5f, 0.5f, -0.5f,
				// V6
				-0.5f, -0.5f, -0.5f,
				// V7
				0.5f, -0.5f, -0.5f,

				// For text coords in top face
				// V8: V4 repeated
				-0.5f, 0.5f, -0.5f,
				// V9: V5 repeated
				0.5f, 0.5f, -0.5f,
				// V10: V0 repeated
				-0.5f, 0.5f, 0.5f,
				// V11: V3 repeated
				0.5f, 0.5f, 0.5f,

				// For text coords in right face
				// V12: V3 repeated
				0.5f, 0.5f, 0.5f,
				// V13: V2 repeated
				0.5f, -0.5f, 0.5f,

				// For text coords in left face
				// V14: V0 repeated
				-0.5f, 0.5f, 0.5f,
				// V15: V1 repeated
				-0.5f, -0.5f, 0.5f,

				// For text coords in bottom face
				// V16: V6 repeated
				-0.5f, -0.5f, -0.5f,
				// V17: V7 repeated
				0.5f, -0.5f, -0.5f,
				// V18: V1 repeated
				-0.5f, -0.5f, 0.5f,
				// V19: V2 repeated
				0.5f, -0.5f, 0.5f,
		};
		float[] textCoords = new float[]{
				0.0f, 0.0f,
				0.0f, 0.5f,
				0.5f, 0.5f,
				0.5f, 0.0f,

				0.0f, 0.0f,
				0.5f, 0.0f,
				0.0f, 0.5f,
				0.5f, 0.5f,

				// For text coords in top face
				0.0f, 0.5f,
				0.5f, 0.5f,
				0.0f, 1.0f,
				0.5f, 1.0f,

				// For text coords in right face
				0.0f, 0.0f,
				0.0f, 0.5f,

				// For text coords in left face
				0.5f, 0.0f,
				0.5f, 0.5f,

				// For text coords in bottom face
				0.5f, 0.0f,
				1.0f, 0.0f,
				0.5f, 0.5f,
				1.0f, 0.5f,
		};
		int[] indices = new int[]{
				// Front face
				0, 1, 3, 3, 1, 2,
				// Top Face
				8, 10, 11, 9, 8, 11,
				// Right face
				12, 13, 7, 5, 12, 7,
				// Left face
				14, 15, 6, 4, 14, 6,
				// Bottom face
				16, 18, 19, 17, 16, 19,
				// Back face
				4, 6, 7, 5, 4, 7,
		};
		Texture texture = scene.getTextureCache().createTexture("textures/cube.png");
		Material material = new Material();
		material.setTexturePath(texture.getTexturePath());
		List<Material> materialList = new ArrayList<>();
		materialList.add(material);

		Mesh mesh = new Mesh(positions, textCoords, indices);
		material.getMeshList().add(mesh);
		Model cubeModel = new Model("cube-model", materialList);
		scene.addModel(cubeModel);

		cubeEntity = new Entity("cube-entity", cubeModel.getId());
		cubeEntity.setPosition(0, 20, 0);
		scene.addEntity(cubeEntity);

		scene.setGuiInstance(new GuiImplementer());
		scene.getCamera().setPosition(0, 2* Chunk.CHUNK_SIZE,0);
	}

	@Override
	public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
		if (inputConsumed) {
			return;
		}

		float move = diffTimeMillis * MOVEMENT_SPEED;
		Camera camera = scene.getCamera();
		if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_W)) {
			camera.moveForward(move);
		} else if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_S)) {
			camera.moveBackwards(move);
		}
		if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_A)) {
			camera.moveLeft(move);
		} else if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_D)) {
			camera.moveRight(move);
		}
		if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_UP)) {
			camera.moveUp(move);
		} else if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_DOWN)) {
			camera.moveDown(move);
		}

		MouseListener mouseInput = window.getMouseListener();
		if (mouseInput.isRightButtonPressed()) {
			Vector2f displVec = mouseInput.getDisplVec();
			camera.addRotation((float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY),
					(float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
		}
	}

	@Override
	public void update(Window window, Scene scene, long diffTimeMillis) {
		rotation += 1.5;
		if (rotation > 360) {
			rotation = 0;
		}
		cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
		cubeEntity.updateModelMatrix();
	}
}
