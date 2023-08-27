package uk.minersonline.Minecart.modules;

import uk.minersonline.Minecart.core.buffers.MeshVBO;
import uk.minersonline.Minecart.core.configs.CCW;
import uk.minersonline.Minecart.core.model.Mesh;
import uk.minersonline.Minecart.core.renderer.RenderInfo;
import uk.minersonline.Minecart.core.renderer.Renderer;
import uk.minersonline.Minecart.core.scene.GameObject;
import uk.minersonline.Minecart.core.utils.Constants;
import uk.minersonline.Minecart.core.utils.objloader.OBJLoader;

public class Skydome extends GameObject{
	
	public Skydome()
	{
		getTransform().setScaling(Constants.ZFAR*0.5f, Constants.ZFAR*0.5f, Constants.ZFAR*0.5f);
		Mesh mesh = new OBJLoader().load("./res/models/dome", "dome.obj", null)[0].getMesh();
		MeshVBO meshBuffer = new MeshVBO();
		meshBuffer.addData(mesh);
		Renderer renderer = new Renderer(meshBuffer);
		renderer.setRenderInfo(new RenderInfo(new CCW(), AtmosphereShader.getInstance()));
		addComponent(Constants.RENDERER_COMPONENT, renderer);
	}
}
