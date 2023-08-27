package uk.minersonline.Minecart;

import uk.minersonline.Minecart.scene.Scene;
import uk.minersonline.Minecart.glfw.window.Window;
import uk.minersonline.Minecart.glfw.Renderer;

public interface MinecartGame {
	void cleanup();

	void init(Window window, Scene scene, Renderer render);

	void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed);

	void update(Window window, Scene scene, long diffTimeMillis);
}
