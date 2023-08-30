package uk.minersonline.HistorySurvival;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import uk.minersonline.Minecart.core.kernel.Camera;
import uk.minersonline.Minecart.core.kernel.Input;
import uk.minersonline.Minecart.core.math.Vec2f;
import uk.minersonline.Minecart.core.math.Vec3f;
import uk.minersonline.Minecart.gui.ControlsManager;
import uk.minersonline.Minecart.gui.IGuiInstance;

public class GuiImplementer implements IGuiInstance {
	private static int location = 0;

	@Override
	public void drawGui() {
		ImGui.newFrame();
		ImGui.setNextWindowPos(0, 0);
		ImGui.showDemoWindow();

		ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);

		ImGuiIO io = ImGui.getIO();

		int window_flags = ImGuiWindowFlags.NoDecoration |
				ImGuiWindowFlags.AlwaysAutoResize |
				ImGuiWindowFlags.NoSavedSettings |
				ImGuiWindowFlags.NoFocusOnAppearing |
				ImGuiWindowFlags.NoNav;

		if (location >= 0) {
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
		else if (location == -2) {
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
			Vec3f position = Camera.getInstance().getPosition();
			ImGui.text("Camera Position: (%.1f,%.1f,%.1f)".formatted(position.X, position.Y, position.Z));
			ImGui.separator();
			ImGui.text("Drawing");
			ImGui.checkbox("Draw Wireframe", ControlsManager.DrawChunkWireframe);
			ImGui.checkbox("Draw Chunk Node Bounds", ControlsManager.DrawChunkNodeBounds);
			ImGui.checkbox("Draw Chunk Seam Bounds", ControlsManager.DrawChunkSeamBounds);
			ImGui.separator();
			ImGui.text("Gameplay");
			if (ImGui.beginPopupContextWindow()) {
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
	public boolean handleGuiInput() {
		ImGuiIO imGuiIO = ImGui.getIO();
		Input input = Input.getInstance();

		Vec2f mousePos = input.getCursorPosition();
		imGuiIO.setMousePos(mousePos.X, mousePos.Y);
		imGuiIO.setMouseDown(0, input.isButtonHolding(0));
		imGuiIO.setMouseDown(1, input.isButtonHolding(1));

		Vec2f scroll = input.getScrollOffset();
		imGuiIO.setMouseWheel(scroll.Y);
		imGuiIO.setMouseWheelH(scroll.X);
		input.setScrollOffset(0, 0);

		return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
	}
}
