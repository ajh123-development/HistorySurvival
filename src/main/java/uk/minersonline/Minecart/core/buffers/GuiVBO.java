package uk.minersonline.Minecart.core.buffers;

import imgui.ImGui;
import uk.minersonline.Minecart.core.model.Mesh;
import uk.minersonline.Minecart.core.texturing.Texture2D;
import uk.minersonline.Minecart.core.utils.BufferUtil;

import imgui.ImDrawData;
import uk.minersonline.Minecart.gui.IGuiInstance;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GuiVBO implements VBO{
	private Texture2D texture;
	private Mesh guiMesh;
	private final IGuiInstance guiInstance;
	protected int vbo;
	protected int ibo;
	protected int vaoId;
	protected int size;

	public GuiVBO(IGuiInstance guiInstance) {
		this.guiInstance = guiInstance;
		vbo = glGenBuffers();
		ibo = glGenBuffers();
		vaoId = glGenVertexArrays();
		size = 0;
	}
	
	public void addData(Mesh mesh) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, ImDrawData.SIZEOF_IM_DRAW_VERT, 0);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, ImDrawData.SIZEOF_IM_DRAW_VERT, 8);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, ImDrawData.SIZEOF_IM_DRAW_VERT, 16);


		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		guiMesh = mesh;
	}
	
	@Override
	public void draw(boolean wireframe) {
		if (guiInstance != null) {
			guiInstance.drawGui();
		}

		if (guiMesh != null) {
			glBindVertexArray(vaoId);

			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

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
		}
	}

	public void setTexture(Texture2D texture) {
		this.texture = texture;
	}

	@Override
	public void delete() {
		glBindVertexArray(vaoId);
		glDeleteBuffers(vbo);
		glDeleteVertexArrays(vaoId);
		glBindVertexArray(0);
	}
}
