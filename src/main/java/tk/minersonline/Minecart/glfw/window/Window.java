package tk.minersonline.Minecart.glfw.window;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import tk.minersonline.Minecart.glfw.window.listener.KeyListener;
import tk.minersonline.Minecart.glfw.window.listener.MouseListener;
import tk.minersonline.Minecart.glfw.window.listener.WindowResizeListener;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.nio.IntBuffer;
import java.util.Objects;
import java.util.concurrent.Callable;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.opengl.GL11.GL_DONT_CARE;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width;
    private int height;
    private long glfwWindowAddress;
    private final Callable<Void> resizeFunc;

    private MouseListener mouseListener = null;

    private final WindowConfig config;

    public Window(WindowConfig config, Callable<Void> resizeFunc) {
        this.config = config;
        this.width = config.getDefaultWidth();
        this.height = config.getDefaultHeight();
        this.resizeFunc = resizeFunc;

        init();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        boolean glfwStarted = glfwInit();

        // Throw error and terminate if GLFW initialization fails
        if (!glfwStarted) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowAddress = createAndConfigureWindow();

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(glfwWindowAddress, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    glfwWindowAddress,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        setListeners();

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindowAddress);

        if (config.getTargetFps() > 0) {
            glfwSwapInterval(0);
        } else {
            glfwSwapInterval(1);
        }

        // Make the window visible
        glfwShowWindow(glfwWindowAddress);
    }

    private long createAndConfigureWindow() {
        // Create the window
        long windowAddress = glfwCreateWindow(this.width, this.height, config.getTitle(), NULL, NULL);

        if (windowAddress == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // tk.minersonline.Minecart.glfw window Configuration
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwSetWindowSizeLimits(windowAddress, config.getMinWidth(), config.getMinHeight(), GL_DONT_CARE, GL_DONT_CARE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        return windowAddress;
    }

    private void setListeners() {
        glfwSetKeyCallback(glfwWindowAddress, KeyListener.getInstance());
        glfwSetWindowSizeCallback(glfwWindowAddress, new WindowResizeListener(this));
        this.mouseListener = new MouseListener(glfwWindowAddress);
    }

    public void cleanup() {
        // Free memory upon leaving
        glfwFreeCallbacks(glfwWindowAddress);
        glfwDestroyWindow(glfwWindowAddress);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public Callable<Void> getResizeFunc() {
        return resizeFunc;
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public void update() {
        glfwSwapBuffers(glfwWindowAddress);
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(glfwWindowAddress);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public MouseListener getMouseListener() {
        return mouseListener;
    }

    public long getGlfwWindowAddress() {
        return glfwWindowAddress;
    }
}
