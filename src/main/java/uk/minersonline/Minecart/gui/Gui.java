package uk.minersonline.Minecart.gui;

import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;
import uk.minersonline.Minecart.core.buffers.GuiVBO;
import uk.minersonline.Minecart.core.configs.GuiConfig;
import uk.minersonline.Minecart.core.kernel.Input;
import uk.minersonline.Minecart.core.kernel.Window;
import uk.minersonline.Minecart.core.model.Mesh;
import uk.minersonline.Minecart.core.renderer.RenderInfo;
import uk.minersonline.Minecart.core.renderer.Renderer;
import uk.minersonline.Minecart.core.scene.GameObject;
import uk.minersonline.Minecart.core.texturing.Texture2D;
import uk.minersonline.Minecart.core.utils.Constants;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;

public class Gui extends GameObject {
	private final Mesh mesh = new Mesh(null, null);
	private final GuiVBO vbo;
	private final IGuiInstance gui;

	public Gui(IGuiInstance gui) {
		this.gui = gui;
		vbo = new GuiVBO(gui);
		vbo.addData(mesh);
		Renderer renderer = new Renderer(vbo);
		renderer.setRenderInfo(new RenderInfo(new GuiConfig(), GuiShader.getInstance()));
		createUIResources();
		addComponent(Constants.RENDERER_COMPONENT, renderer);
	}

	@Override
	public void update() {
		super.update();
		gui.handleGuiInput();
	}

	private void createUIResources() {
		Window window = Window.getInstance();
		ImGui.createContext();

		ImGuiIO imGuiIO = ImGui.getIO();
		imGuiIO.setIniFilename(null);
		imGuiIO.setDisplaySize(window.getWidth(), window.getHeight());

		ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
		ImInt width = new ImInt();
		ImInt height = new ImInt();
		ByteBuffer buf = fontAtlas.getTexDataAsRGBA32(width, height);
		Texture2D texture = new Texture2D(width.get(), height.get(), buf);
		vbo.setTexture(texture);
	}
}
