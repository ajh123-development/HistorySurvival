package uk.minersonline.Minecart.gui;

import uk.minersonline.Minecart.core.math.Vec2f;
import uk.minersonline.Minecart.core.shaders.Shader;
import uk.minersonline.Minecart.core.utils.ResourceLoader;

public class GuiShader extends Shader {

	private static GuiShader instance = null;
	private final Vec2f scale;

	public static GuiShader getInstance() {
		if(instance == null) {
			instance = new GuiShader();
		}
		return instance;
	}

	protected GuiShader() {
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/gui_VS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/gui_FS.glsl"));
		compileShader();

		addUniform("scale");
		scale = new Vec2f();
	}
}
