package uk.minersonline.Minecart.core.configs;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by proton2 on 31.12.2019.
 */
public class CW implements RenderConfig{
    public void enable(){
        glFrontFace(GL_CW);
    }

    public void disable(){
        glFrontFace(GL_CCW);
    }
}
