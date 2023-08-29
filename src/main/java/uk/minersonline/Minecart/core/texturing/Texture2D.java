package uk.minersonline.Minecart.core.texturing;

import uk.minersonline.Minecart.core.utils.ImageLoader;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture2D {
	
	private int id;
	private int width;
	private int height;
	
	public Texture2D(){}

	public Texture2D(int width, int height, ByteBuffer buf) {
		generateTexture(width, height, buf);
	}


	public Texture2D(String file)
	{
		id = ImageLoader.loadImage(file);
	}
	
	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public void generate()
	{
		id = glGenTextures();
	}
	
	public void delete()
	{
		glDeleteTextures(id);
	}
	
	public void unbind()
	{
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public int getId() {
		return id;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
