package tk.minersonline.Minecart.glfw;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import tk.minersonline.Minecart.glfw.listener.KeyListener;
import tk.minersonline.Minecart.glfw.listener.WindowResizeListener;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import tk.minersonline.Minecart.util.Color;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static tk.minersonline.Minecart.geometry.configuration.World.setCoordinatePlane;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DONT_CARE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.system.MemoryUtil.NULL;
import static tk.minersonline.Minecart.util.Color.WHITE;

public class Window {
    private int width;
    private int height;
    private long glfwWindowAddress;

    private static Window INSTANCE = null;

    private final WindowConfig config;
    private static final Color BACKGROUND_COLOR = WHITE;

    private Window(WindowConfig config) {
        this.config = config;
        this.width = config.getDefaultWidth();
        this.height = config.getDefaultHeight();
    }

    public static Window getInstance(WindowConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new Window(config);
        }
        return INSTANCE;
    }

    public void run() {
        init();
        execution();
        terminateGracefully();
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

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindowAddress);


        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        setCoordinatePlane();
        setListeners();
        // Make the window visible
        glfwShowWindow(glfwWindowAddress);
    }


    private void execution() {
        // This is the main loop
        while (!glfwWindowShouldClose(glfwWindowAddress)) {
            keyListenerExample();
            renderSampleSquare();
            glfwPollEvents();
        }
    }

    /**
    * This is an example on how to use the implemented {@link KeyListener}
    * In this example, color is set from red to blue while the SPACE key is pressed on the keyboard
    * */
    private void keyListenerExample() {
        if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_SPACE)) {
            glColor3f(0, 0, 1f);
        } else {
            glColor3f(1f, 0, 0);
        }
    }

    private void renderSampleSquare() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(BACKGROUND_COLOR.getRed(), BACKGROUND_COLOR.getGreen(), BACKGROUND_COLOR.getBlue(), 0.0f);

        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(0, -1f);
        glVertex2f(1f, -1f);
        glVertex2f(1f, 0);
        glEnd();

        glfwSwapBuffers(glfwWindowAddress);
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
        glfwSetWindowSizeCallback(glfwWindowAddress, WindowResizeListener.getInstance(config));
    }

    private void terminateGracefully() {
        // Free memory upon leaving
        glfwFreeCallbacks(glfwWindowAddress);
        glfwDestroyWindow(glfwWindowAddress);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
