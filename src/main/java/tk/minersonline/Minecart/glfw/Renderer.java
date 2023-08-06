package tk.minersonline.Minecart.glfw;

import org.lwjgl.opengl.GL;
import tk.minersonline.Minecart.glfw.window.Window;
import tk.minersonline.Minecart.scene.Scene;
import tk.minersonline.Minecart.scene.SceneRenderer;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
	private final SceneRenderer sceneRenderer;

	public Renderer() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		sceneRenderer = new SceneRenderer();
	}

	public void cleanup() {
		sceneRenderer.cleanup();
	}

	public void render(Window window, Scene scene) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, window.getWidth(), window.getHeight());
		sceneRenderer.render(scene);
	}
}
