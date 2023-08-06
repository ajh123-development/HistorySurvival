package tk.minersonline.Minecart;

import tk.minersonline.Minecart.scene.Scene;
import tk.minersonline.Minecart.glfw.window.Window;
import tk.minersonline.Minecart.glfw.Renderer;

public interface MinecartGame {
	void cleanup();

	void init(Window window, Scene scene, Renderer render);

	void input(Window window, Scene scene, long diffTimeMillis);

	void update(Window window, Scene scene, long diffTimeMillis);
}
