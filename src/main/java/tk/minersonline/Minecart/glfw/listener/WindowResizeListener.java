package tk.minersonline.Minecart.glfw.listener;

import tk.minersonline.Minecart.glfw.Window;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import tk.minersonline.Minecart.glfw.WindowConfig;

import static tk.minersonline.Minecart.geometry.configuration.World.setCoordinatePlane;
import static org.lwjgl.glfw.GLFW.glfwSetWindowAspectRatio;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glViewport;

public class WindowResizeListener implements GLFWWindowSizeCallbackI {
    private static WindowResizeListener INSTANCE;
    private final WindowConfig config;

    private WindowResizeListener(WindowConfig config) {
        this.config = config;
    }

    public static WindowResizeListener getInstance(WindowConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new WindowResizeListener(config);
        }
        return INSTANCE;
    }

    @Override
    public void invoke(long window, int width, int height) {
        reshape(window, width, height);
        Window windowObj = Window.getInstance(config);
        windowObj.setWidth(width);
        windowObj.setHeight(height);
    }

    private void reshape(long window, int width, int height) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        glViewport(0, 0, width, height);
        setCoordinatePlane();
        glfwSetWindowAspectRatio(window, width, height);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }
}
