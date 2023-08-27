package uk.minersonline.HistorySurvival;

import uk.minersonline.Minecart.MinecartEngine;
import uk.minersonline.Minecart.glfw.window.WindowConfig;
import tk.minersonline.Minecart.scene.objects.*;
import uk.minersonline.Minecart.scene.views.ProjectionView;

import static de.damios.guacamole.gdx.StartOnFirstThreadHelper.startNewJvmIfRequired;

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
