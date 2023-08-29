package uk.minersonline.Minecart.gui;

import uk.minersonline.Minecart.core.scene.GameObject;
import uk.minersonline.Minecart.core.shaders.Shader;
import uk.minersonline.Minecart.core.utils.ResourceLoader;

public class GuiShader extends Shader {

	private static GuiShader instance = null;

	public static GuiShader getInstance()
	{
		if(instance == null)
		{
			instance = new GuiShader();
		}
		return instance;
	}

	protected GuiShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/gui_VS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/guie_FS.glsl"));
		compileShader();

		addUniform("modelViewProjectionMatrix");
		addUniform("worldMatrix");
	}

	public void updateUniforms(GameObject object)
	{
		setUniform("modelViewProjectionMatrix", object.getTransform().getModelViewProjectionMatrix());
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
	}
}
