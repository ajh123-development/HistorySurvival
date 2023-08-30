package uk.minersonline.Minecart.core.configs;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;


public class GuiConfig implements RenderConfig{

	public void enable() {
		glFrontFace(GL_FRONT);
		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
	}

	public void disable() {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glDisable(GL_BLEND);
	}
	
	public static void init() {

	}
}
