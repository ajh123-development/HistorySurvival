package uk.minersonline.Minecart.gui;

import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.type.ImInt;
import uk.minersonline.Minecart.core.buffers.GuiVBO;
import uk.minersonline.Minecart.core.configs.CCW;
import uk.minersonline.Minecart.core.kernel.Window;
import uk.minersonline.Minecart.core.model.Mesh;
import uk.minersonline.Minecart.core.renderer.RenderInfo;
import uk.minersonline.Minecart.core.renderer.Renderer;
import uk.minersonline.Minecart.core.scene.GameObject;
import uk.minersonline.Minecart.core.texturing.Texture2D;
import uk.minersonline.Minecart.core.utils.Constants;

import java.nio.ByteBuffer;

public class Gui extends GameObject {
	private final Mesh mesh = new Mesh(null, null);

	public Gui()
	{
		GuiVBO meshBuffer = new GuiVBO();
		meshBuffer.addData(mesh);
		Renderer renderer = new Renderer(meshBuffer);
		renderer.setRenderInfo(new RenderInfo(new CCW(), GuiShader.getInstance()));
		addComponent(Constants.RENDERER_COMPONENT, renderer);
	}

	@Override
	public void update() {
		super.update();
	}

	private void createUIResources() {
		Window window = Window.getInstance()
		ImGui.createContext();

		ImGuiIO imGuiIO = ImGui.getIO();
		imGuiIO.setIniFilename(null);
		imGuiIO.setDisplaySize(window.getWidth(), window.getHeight());

		ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
		ImInt width = new ImInt();
		ImInt height = new ImInt();
		ByteBuffer buf = fontAtlas.getTexDataAsRGBA32(width, height);
		texture = new Texture2D(width.get(), height.get(), buf);

		guiMesh = new GuiMesh();
	}
}
