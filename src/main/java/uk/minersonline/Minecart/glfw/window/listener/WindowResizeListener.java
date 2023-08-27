package uk.minersonline.Minecart.glfw.window.listener;

import uk.minersonline.Minecart.glfw.window.Window;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

public class WindowResizeListener implements GLFWWindowSizeCallbackI {
    private final Window window;

    public WindowResizeListener(Window window) {
        this.window = window;
    }

    @Override
    public void invoke(long window, int width, int height) {
        this.window.setWidth(width);
        this.window.setHeight(height);
        try {
            this.window.getResizeFunc().call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
