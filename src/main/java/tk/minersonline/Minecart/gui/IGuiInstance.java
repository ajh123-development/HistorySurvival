package tk.minersonline.Minecart.gui;

import tk.minersonline.Minecart.glfw.window.Window;
import tk.minersonline.Minecart.scene.Scene;

public interface IGuiInstance {
	void drawGui();

	boolean handleGuiInput(Scene scene, Window window);
}