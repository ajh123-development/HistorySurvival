package uk.minersonline.Minecart.core.kernel;

import uk.minersonline.Minecart.core.configs.Default;
import uk.minersonline.Minecart.gui.Gui;
import uk.minersonline.Minecart.gui.IGuiInstance;
import uk.minersonline.Minecart.terrain.ChunkOctreeWrapper;

import static org.lwjgl.opengl.GL11.glViewport;

/**
 * 
 * @author oreon3D
 * The RenderingEngine manages the render calls of all 3D entities
 * with shadow rendering and post processing effects
 *
 */
public class RenderingEngine {
	private final Window window;
	//private Skydome skydome;
	//private DcWrapper dcWrapper;
	private final Gui gui;
	private final ChunkOctreeWrapper chunkOctreeWrapper;
	
	public RenderingEngine(IGuiInstance gui) {
		window = Window.getInstance();
		this.gui = new Gui(gui);
		//skydome = new Skydome();
		//dcWrapper = new DcWrapper();
		chunkOctreeWrapper = new ChunkOctreeWrapper();
	}
	
	public void init() {
		window.init();
	}

	public void render() {
		Default.clearScreen();
		glViewport(0, 0, window.getWidth(), window.getHeight());
		
		//skydome.render();
		//dcWrapper.render();
		chunkOctreeWrapper.render();

		gui.render();
		// draw into OpenGL window
		window.render();
	}
	
	public void update() {
		gui.update();
		Camera.getInstance().update();
		//dcWrapper.update();
		chunkOctreeWrapper.update();
	}
	
	public void shutdown() {
		chunkOctreeWrapper.cleanUp();
	}
}
