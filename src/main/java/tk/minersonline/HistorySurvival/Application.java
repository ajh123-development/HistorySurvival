package tk.minersonline.HistorySurvival;

import tk.minersonline.Minecart.MinecartEngine;
import tk.minersonline.Minecart.MinecartGame;
import tk.minersonline.Minecart.glfw.window.Window;
import tk.minersonline.Minecart.glfw.window.WindowConfig;
import tk.minersonline.Minecart.scene.Scene;
import tk.minersonline.Minecart.glfw.Renderer;
import tk.minersonline.Minecart.scene.objects.Mesh;

import static de.damios.guacamole.gdx.StartOnFirstThreadHelper.startNewJvmIfRequired;

public class Application implements MinecartGame {

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
        MinecartEngine engine = new MinecartEngine(CONFIG, new Application());
        engine.start();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void init(Window window, Scene scene, Renderer render) {
        float[] positions = new float[]{
                0.0f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };
        Mesh mesh = new Mesh(positions, 3);
        scene.addMesh("triangle", mesh);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {

    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {

    }
}
