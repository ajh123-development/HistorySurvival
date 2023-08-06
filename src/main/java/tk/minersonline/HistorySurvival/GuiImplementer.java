package tk.minersonline.HistorySurvival;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import tk.minersonline.Minecart.glfw.window.Window;
import tk.minersonline.Minecart.glfw.window.listener.MouseListener;
import tk.minersonline.Minecart.gui.IGuiInstance;
import tk.minersonline.Minecart.scene.Scene;

public class GuiImplementer implements IGuiInstance {
	@Override
	public void drawGui() {
		ImGui.newFrame();
		ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
		ImGui.showDemoWindow();
		ImGui.endFrame();
		ImGui.render();
	}

	@Override
	public boolean handleGuiInput(Scene scene, Window window) {
		ImGuiIO imGuiIO = ImGui.getIO();
		MouseListener mouseInput = window.getMouseListener();
		Vector2f mousePos = mouseInput.getCurrentPos();
		imGuiIO.setMousePos(mousePos.x, mousePos.y);
		imGuiIO.setMouseDown(0, mouseInput.isLeftButtonPressed());
		imGuiIO.setMouseDown(1, mouseInput.isRightButtonPressed());

		return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
	}
}
