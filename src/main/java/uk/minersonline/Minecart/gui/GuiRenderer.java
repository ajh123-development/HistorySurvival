package uk.minersonline.Minecart.gui;

import imgui.ImDrawData;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;
import org.joml.Vector2f;
import uk.minersonline.Minecart.glfw.shaders.ShaderModuleData;
import uk.minersonline.Minecart.glfw.shaders.ShaderProgram;
import uk.minersonline.Minecart.glfw.shaders.UniformsMap;
import uk.minersonline.Minecart.glfw.window.Window;
import uk.minersonline.Minecart.glfw.window.listener.KeyListener;
import uk.minersonline.Minecart.scene.Scene;
import uk.minersonline.Minecart.scene.objects.Texture;

import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL32.*;

public class GuiRenderer {

	private GuiMesh guiMesh;
	private Vector2f scale;
	private ShaderProgram shaderProgram;
	private Texture texture;
	private UniformsMap uniformsMap;

	public GuiRenderer(Window window) {
		List<ShaderModuleData> shaderModuleDataList = new ArrayList<>();
		shaderModuleDataList.add(new ShaderModuleData("shaders/gui/shader.vert", GL_VERTEX_SHADER));
		shaderModuleDataList.add(new ShaderModuleData("shaders/gui/shader.frag", GL_FRAGMENT_SHADER));
		shaderProgram = new ShaderProgram(shaderModuleDataList);
		createUniforms();
		createUIResources(window);
		setupKeyCallBack(window);
	}

	public void cleanup() {
		shaderProgram.cleanup();
		texture.cleanup();
	}

	private void createUIResources(Window window) {
		ImGui.createContext();

		ImGuiIO imGuiIO = ImGui.getIO();
		imGuiIO.setIniFilename(null);
		imGuiIO.setDisplaySize(window.getWidth(), window.getHeight());

		ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
		ImInt width = new ImInt();
		ImInt height = new ImInt();
		ByteBuffer buf = fontAtlas.getTexDataAsRGBA32(width, height);
		texture = new Texture(width.get(), height.get(), buf);

		guiMesh = new GuiMesh();
	}

	private void createUniforms() {
		uniformsMap = new UniformsMap(shaderProgram.getProgramId());
		uniformsMap.createUniform("scale");
		scale = new Vector2f();
	}

	private void setupKeyCallBack(Window window) {
		ImGuiIO io = ImGui.getIO();
		io.setKeyMap(ImGuiKey.Tab, GLFW_KEY_TAB);
		io.setKeyMap(ImGuiKey.LeftArrow, GLFW_KEY_LEFT);
		io.setKeyMap(ImGuiKey.RightArrow, GLFW_KEY_RIGHT);
		io.setKeyMap(ImGuiKey.UpArrow, GLFW_KEY_UP);
		io.setKeyMap(ImGuiKey.DownArrow, GLFW_KEY_DOWN);
		io.setKeyMap(ImGuiKey.PageUp, GLFW_KEY_PAGE_UP);
		io.setKeyMap(ImGuiKey.PageDown, GLFW_KEY_PAGE_DOWN);
		io.setKeyMap(ImGuiKey.Home, GLFW_KEY_HOME);
		io.setKeyMap(ImGuiKey.End, GLFW_KEY_END);
		io.setKeyMap(ImGuiKey.Insert, GLFW_KEY_INSERT);
		io.setKeyMap(ImGuiKey.Delete, GLFW_KEY_DELETE);
		io.setKeyMap(ImGuiKey.Backspace, GLFW_KEY_BACKSPACE);
		io.setKeyMap(ImGuiKey.Space, GLFW_KEY_SPACE);
		io.setKeyMap(ImGuiKey.Enter, GLFW_KEY_ENTER);
		io.setKeyMap(ImGuiKey.Escape, GLFW_KEY_ESCAPE);
		io.setKeyMap(ImGuiKey.KeyPadEnter, GLFW_KEY_KP_ENTER);

		glfwSetKeyCallback(window.getGlfwWindowAddress(), (handle, key, scancode, action, mods) -> {
			KeyListener.getInstance().invoke(handle, key, scancode, action, mods);
			if (!io.getWantCaptureKeyboard()) {
				return;
			}
			if (action == GLFW_PRESS) {
				io.setKeysDown(key, true);
			} else if (action == GLFW_RELEASE) {
				io.setKeysDown(key, false);
			}
			io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
			io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
			io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
			io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
		});

		glfwSetCharCallback(window.getGlfwWindowAddress(), (handle, c) -> {
			if (!io.getWantCaptureKeyboard()) {
				return;
			}
			io.addInputCharacter(c);
		});
	}

	public void render(Scene scene) {
		IGuiInstance guiInstance = scene.getGuiInstance();
		if (guiInstance == null) {
			return;
		}
		guiInstance.drawGui(scene);

		shaderProgram.bind();

		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		glBindVertexArray(guiMesh.getVaoId());

		glBindBuffer(GL_ARRAY_BUFFER, guiMesh.getVerticesVBO());
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, guiMesh.getIndicesVBO());

		ImGuiIO io = ImGui.getIO();
		scale.x = 2.0f / io.getDisplaySizeX();
		scale.y = -2.0f / io.getDisplaySizeY();
		uniformsMap.setUniform("scale", scale);

		ImDrawData drawData = ImGui.getDrawData();
		int numLists = drawData.getCmdListsCount();
		for (int i = 0; i < numLists; i++) {
			glBufferData(GL_ARRAY_BUFFER, drawData.getCmdListVtxBufferData(i), GL_STREAM_DRAW);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, drawData.getCmdListIdxBufferData(i), GL_STREAM_DRAW);

			int numCmds = drawData.getCmdListCmdBufferSize(i);
			for (int j = 0; j < numCmds; j++) {
				final int elemCount = drawData.getCmdListCmdBufferElemCount(i, j);
				final int idxBufferOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);
				final int indices = idxBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;

				texture.bind();
				glDrawElements(GL_TRIANGLES, elemCount, GL_UNSIGNED_SHORT, indices);
			}
		}

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glDisable(GL_BLEND);
	}

	public void resize(int width, int height) {
		ImGuiIO imGuiIO = ImGui.getIO();
		imGuiIO.setDisplaySize(width, height);
	}
}