package uk.minersonline.Minecart.gui;

import uk.minersonline.Minecart.glfw.window.Window;
import uk.minersonline.Minecart.scene.Scene;

public interface IGuiInstance {
	void drawGui(Scene scene);

	boolean handleGuiInput(Scene scene, Window window);
}