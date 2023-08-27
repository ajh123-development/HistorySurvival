package uk.minersonline.Minecart.glfw;

import org.lwjgl.opengl.GL;
import uk.minersonline.Minecart.glfw.window.Window;
import uk.minersonline.Minecart.gui.GuiRenderer;
import uk.minersonline.Minecart.scene.Scene;
import uk.minersonline.Minecart.scene.SceneRenderer;
import uk.minersonline.Minecart.scene.terrain.WorldRenderer;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
	private final SceneRenderer sceneRenderer;
	private final WorldRenderer worldRenderer;
	private final GuiRenderer guiRenderer;

	public Renderer(Window window) {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		sceneRenderer = new SceneRenderer();
		worldRenderer = new WorldRenderer();
		guiRenderer = new GuiRenderer(window);
	}

	public void cleanup() {
		sceneRenderer.cleanup();
		guiRenderer.cleanup();
	}

	public void render(Window window, Scene scene) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, window.getWidth(), window.getHeight());
		worldRenderer.render(scene.getWorld());
		sceneRenderer.render(scene);
		guiRenderer.render(scene);
	}

	public void resize(int width, int height) {
		guiRenderer.resize(width, height);
	}
}