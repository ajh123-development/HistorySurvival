package tk.minersonline.HistorySurvival;

import tk.minersonline.Minecart.glfw.Window;
import tk.minersonline.Minecart.glfw.WindowConfig;

import static de.damios.guacamole.gdx.StartOnFirstThreadHelper.startNewJvmIfRequired;

public class Application {

    private static final WindowConfig CONFIG = new WindowConfig(
            800,
            600,
            400,
            600,
            "History Survival"
    );
    private static final Window WINDOW = Window.getInstance(CONFIG);

    public static void main(String[] args) {
        // Starts a new JVM if the application was started on macOS without the
        // -XstartOnFirstThread argument.
        if (startNewJvmIfRequired()) {
            System.exit(0);
        }
        WINDOW.run();
    }
}
