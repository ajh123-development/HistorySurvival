package uk.minersonline.Minecart.core.configs;

import static org.lwjgl.opengl.GL11.*;

public class CCW implements RenderConfig{
	
	public void enable(){
		glFrontFace(GL_CCW);
	}

	public void disable(){
		glFrontFace(GL_CW);
	}
}
