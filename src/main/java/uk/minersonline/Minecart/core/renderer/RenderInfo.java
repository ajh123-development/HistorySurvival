package uk.minersonline.Minecart.core.renderer;

import uk.minersonline.Minecart.core.configs.RenderConfig;
import uk.minersonline.Minecart.core.shaders.Shader;

public class RenderInfo {
	
	private RenderConfig config;
	private Shader shader;
	
	public RenderInfo(RenderConfig config, Shader shader){
		this.config = config;
		this.shader = shader;
	}
	
	public RenderConfig getConfig() {
		return config;
	}
	public void setConfig(RenderConfig config) {
		this.config = config;
	}
	public Shader getShader() {
		return shader;
	}
	public void setShader(Shader shader) {
		this.shader = shader;
	}
}
