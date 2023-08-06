package tk.minersonline.HistorySurvival;

import org.joml.Vector3f;
import org.joml.Vector4f;
import tk.minersonline.Minecart.MinecartEngine;
import tk.minersonline.Minecart.MinecartGame;
import tk.minersonline.Minecart.glfw.window.Window;
import tk.minersonline.Minecart.glfw.window.WindowConfig;
import tk.minersonline.Minecart.glfw.window.listener.KeyListener;
import tk.minersonline.Minecart.scene.Scene;
import tk.minersonline.Minecart.glfw.Renderer;
import tk.minersonline.Minecart.scene.objects.Entity;
import tk.minersonline.Minecart.scene.objects.Mesh;
import tk.minersonline.Minecart.scene.objects.Model;
import tk.minersonline.Minecart.scene.views.ProjectionView;

import java.util.ArrayList;
import java.util.List;

import static de.damios.guacamole.gdx.StartOnFirstThreadHelper.startNewJvmIfRequired;
import static org.lwjgl.glfw.GLFW.*;

public class Application implements MinecartGame {
    private Entity cubeEntity;
    private Vector4f displacement = new Vector4f();
    private float rotation;

    private static final WindowConfig CONFIG = new WindowConfig(
            800,
            600,
            400,
            600,
            "History Survival",
            60,
            30
    );

    public static void main(String[] args) {
        // Starts a new JVM if the application was started on macOS without the
        // -XstartOnFirstThread argument.
        if (startNewJvmIfRequired()) {
            System.exit(0);
        }
        MinecartEngine engine = new MinecartEngine(
            CONFIG,
            new Application(),
            new ProjectionView(
                CONFIG.getDefaultWidth(),
                CONFIG.getDefaultHeight()
            )
        );
        engine.start();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void init(Window window, Scene scene, Renderer render) {
        float[] positions = new float[]{
                // VO
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
        };
        float[] colors = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };
        List<Mesh> meshList = new ArrayList<>();
        Mesh mesh = new Mesh(positions, colors, indices);
        meshList.add(mesh);
        String cubeModelId = "cube-model";
        Model model = new Model(cubeModelId, meshList);
        scene.addModel(model);

        cubeEntity = new Entity("cube-entity", cubeModelId);
        cubeEntity.setPosition(0, 0, -2);
        scene.addEntity(cubeEntity);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        displacement.zero();
        if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_UP)) {
            displacement.y = 1;
        } else if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_DOWN)) {
            displacement.y = -1;
        }
        if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_LEFT)) {
            displacement.x = -1;
        } else if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_RIGHT)) {
            displacement.x = 1;
        }
        if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_A)) {
            displacement.z = -1;
        } else if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_Q)) {
            displacement.z = 1;
        }
        if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_Z)) {
            displacement.w = -1;
        } else if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_X)) {
            displacement.w = 1;
        }

        displacement.mul(diffTimeMillis / 1000.0f);

        Vector3f entityPos = cubeEntity.getPosition();
        cubeEntity.setPosition(displacement.x + entityPos.x, displacement.y + entityPos.y, displacement.z + entityPos.z);
        cubeEntity.setScale(cubeEntity.getScale() + displacement.w);
        cubeEntity.updateModelMatrix();
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
