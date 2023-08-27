package uk.minersonline.Minecart.core.configs;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;


public class Default implements RenderConfig{

	public void enable() {
		
	}

	public void disable() {
		
	}
	
	public static void init() {
		glFrontFace(GL_CW);				
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glEnable(GL_DEPTH_TEST);     	
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_FRAMEBUFFER_SRGB);
	}

	public static void clearScreen() {
		glClearColor(0.0f,0.0f,0.0f,1.0f);
		glClearDepth(1.0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
}
