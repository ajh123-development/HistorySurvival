package uk.minersonline.Minecart.core.buffers;

import uk.minersonline.Minecart.core.model.Mesh;
import uk.minersonline.Minecart.core.utils.BufferUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class MeshVBO implements VBO{

	protected int vbo;
	protected int ibo;
	protected int vaoId;
	protected int size;
	
	public MeshVBO()
	{
		vbo = glGenBuffers();
		ibo = glGenBuffers();
		vaoId = glGenVertexArrays();
		size = 0;
	}
	
	public void addData(Mesh mesh) {
		size = mesh.getIndices().length;

		glBindVertexArray(vaoId);

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, BufferUtil.createFlippedBufferAOS(mesh.getVertices()), GL_STATIC_DRAW);


		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtil.createFlippedBuffer(mesh.getIndices()), GL_STATIC_DRAW);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES * 8, 0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, Float.BYTES * 8, Float.BYTES * 3);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, Float.BYTES * 8, Float.BYTES * 6);

		glBindVertexArray(0);
	}
	
	@Override
	public void draw(boolean wireframe)
	{
		glBindVertexArray(vaoId);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);

		glDrawElements(GL_TRIANGLES, size, GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);

		glBindVertexArray(0);
	}

	@Override
	public void delete() {
		glBindVertexArray(vaoId);
		glDeleteBuffers(vbo);
		glDeleteVertexArrays(vaoId);
		glBindVertexArray(0);
	}
}
