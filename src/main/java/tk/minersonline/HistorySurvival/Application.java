package tk.minersonline.HistorySurvival;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import tk.minersonline.Minecart.MinecartEngine;
import tk.minersonline.Minecart.MinecartGame;
import tk.minersonline.Minecart.glfw.window.Window;
import tk.minersonline.Minecart.glfw.window.WindowConfig;
import tk.minersonline.Minecart.glfw.window.listener.KeyListener;
import tk.minersonline.Minecart.glfw.window.listener.MouseListener;
import tk.minersonline.Minecart.scene.Camera;
import tk.minersonline.Minecart.scene.Scene;
import tk.minersonline.Minecart.glfw.Renderer;
import tk.minersonline.Minecart.scene.objects.*;
import tk.minersonline.Minecart.scene.views.ProjectionView;

import java.util.ArrayList;
import java.util.List;

import static de.damios.guacamole.gdx.StartOnFirstThreadHelper.startNewJvmIfRequired;
import static org.lwjgl.glfw.GLFW.*;

public class Application {

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
            new GameImplementer(),
            new ProjectionView(
                CONFIG.getDefaultWidth(),
                CONFIG.getDefaultHeight()
            )
        );
        engine.start();
    }
}
