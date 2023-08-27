package uk.minersonline.HistorySurvival;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Vector2f;
import org.joml.Vector3f;
import uk.minersonline.Minecart.glfw.window.Window;
import uk.minersonline.Minecart.glfw.window.listener.MouseListener;
import uk.minersonline.Minecart.gui.IGuiInstance;
import uk.minersonline.Minecart.scene.Scene;
import uk.minersonline.Minecart.scene.terrain.WorldRenderer;

public class GuiImplementer implements IGuiInstance {
	private static int location = 0;
	private static ImBoolean FILL_POLYGON = new ImBoolean(WorldRenderer.FILL_POLYGON);

	@Override
	public void drawGui(Scene scene) {
		ImGui.newFrame();
		ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);

		ImGuiIO io = ImGui.getIO();

		int window_flags = ImGuiWindowFlags.NoDecoration |
				ImGuiWindowFlags.AlwaysAutoResize |
				ImGuiWindowFlags.NoSavedSettings |
				ImGuiWindowFlags.NoFocusOnAppearing |
				ImGuiWindowFlags.NoNav;

		if (location >= 0)
		{
        float PAD = 10.0f;
        ImGuiViewport viewport = ImGui.getMainViewport();
			ImVec2 work_pos = viewport.getWorkPos(); // Use work area to avoid menu-bar/task-bar, if any!
			ImVec2 work_size = viewport.getWorkSize();
			ImVec2 window_pos = new ImVec2(), window_pos_pivot = new ImVec2();
			window_pos.x = (location == 1) ? (work_pos.x + work_size.x - PAD) : (work_pos.x + PAD);
			window_pos.y = (location == 2) ? (work_pos.y + work_size.y - PAD) : (work_pos.y + PAD);
			window_pos_pivot.x = (location & 1) != 0 ? 1.0f : 0.0f;
			window_pos_pivot.y = (location & 2) != 0 ? 1.0f : 0.0f;
			ImGui.setNextWindowPos(window_pos.x, window_pos.y, ImGuiCond.Always, window_pos_pivot.x, window_pos_pivot.y);
			window_flags |= ImGuiWindowFlags.NoMove;
		}
		else if (location == -2)
		{
			// Center window
			ImVec2 center = ImGui.getMainViewport().getCenter();
			ImVec2 pivot = new ImVec2(0.5f, 0.5f);
			ImGui.setNextWindowPos(center.x, center.y, ImGuiCond.Always, pivot.x, pivot.y);
			window_flags |= ImGuiWindowFlags.NoMove;
		}

		ImGui.setNextWindowBgAlpha(0.35f); // Transparent background
		if (ImGui.begin("History Survival Debug", new ImBoolean(true), window_flags)) {
			ImGui.text("History Survival Debug");
			ImGui.separator();
			Vector3f position = scene.getCamera().getPosition();
			ImGui.text("Camera Position: (%.1f,%.1f,%.1f)".formatted(position.x, position.y, position.z));
			ImGui.separator();
			if (ImGui.checkbox("Fill polygons", FILL_POLYGON)) {
				WorldRenderer.FILL_POLYGON = FILL_POLYGON.get();
			}
			if (ImGui.beginPopupContextWindow())
			{
				if (ImGui.menuItem("Custom",       null, location == -1)) location = -1;
				if (ImGui.menuItem("Center",       null, location == -2)) location = -2;
				if (ImGui.menuItem("Top-left",     null, location == 0)) location = 0;
				if (ImGui.menuItem("Top-right",    null, location == 1)) location = 1;
				if (ImGui.menuItem("Bottom-left",  null, location == 2)) location = 2;
				if (ImGui.menuItem("Bottom-right", null, location == 3)) location = 3;
				ImGui.endPopup();
			}
		}
		ImGui.end();


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
