package uk.minersonline.Minecart.core.renderer;

import uk.minersonline.Minecart.core.buffers.VBO;
import uk.minersonline.Minecart.core.scene.Component;

public class Renderer extends Component{
	
	private VBO vbo;
	private RenderInfo renderInfo;
	
	public Renderer(VBO vao) {
		this.vbo = vao;
	}

	public Renderer(VBO vao, RenderInfo renderInfo) {
		this.vbo = vao;
		this.renderInfo = renderInfo;
	}
	
	public void render(){
		renderInfo.getConfig().enable();
		renderInfo.getShader().bind();			
		renderInfo.getShader().updateUniforms(getParent());
		getVbo().draw(true);
		renderInfo.getConfig().disable();
	};

	public VBO getVbo() {
		return vbo;
	}

	public void setVbo(VBO vbo) {
		this.vbo = vbo;
	}

	public RenderInfo getRenderInfo() {
		return renderInfo;
	}

	public void setRenderInfo(RenderInfo renderinfo) {
		this.renderInfo = renderinfo;
	}
}
