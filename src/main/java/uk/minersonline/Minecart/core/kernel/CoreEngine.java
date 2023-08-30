package uk.minersonline.Minecart.core.kernel;

import uk.minersonline.Minecart.core.configs.Default;
import uk.minersonline.Minecart.core.utils.Constants;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
import uk.minersonline.Minecart.gui.GuiRenderer;
import uk.minersonline.Minecart.gui.IGuiInstance;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FILL;

/**
 * 
 * @author oreon3D
 * CoreEngine implements the game loop and manages window close requests.
 * On close request the CoreEngine ensures a clean shutdown of the
 * RenderingEngine and modules.
 *
 */
public class CoreEngine {
	private int targetFps;
	private int targetUps;
	private boolean isRunning;
	private RenderingEngine renderingEngine;
	private IGuiInstance guiInstance;
	private GuiRenderer guiRenderer;

	@SuppressWarnings("unused")
	private GLFWErrorCallback errorCallback;

	public void createWindow(WindowConfig config, IGuiInstance gui) {
		targetFps = config.getTargetFps();
		targetUps = config.getTargetUps();

		glfwInit();
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		
		Window.getInstance().create(config);
		
		renderingEngine = new RenderingEngine();
		guiInstance = gui;
		guiRenderer = new GuiRenderer();
		
		getDeviceProperties();
	}
	
	public void init() {
		Default.init();
		renderingEngine.init();
	}
	
	public void start() {
		if(isRunning)
			return;
		
		run();
	}

	public void run() {
		this.isRunning = true;
		update(0);

		long initialTime = System.currentTimeMillis();
		float timeU = 1000.0f / targetUps;
		float timeR = targetFps > 0 ? 1000.0f / targetFps : 0;
		float deltaUpdate = 0;
		float deltaFps = 0;

		long updateTime = initialTime;
		while (isRunning) {
			glfwPollEvents();


			if (Window.getInstance().isCloseRequested()) {
				stop();
			}

			long now = System.currentTimeMillis();
			deltaUpdate += (now - initialTime) / timeU;
			deltaFps += (now - initialTime) / timeR;

			if (targetFps <= 0 || deltaFps >= 1) {
				Input.getInstance().update();
				boolean inputConsumed = guiInstance.handleGuiInput();
				if (!inputConsumed) {
					Camera.getInstance().update();
				}
			}

			if (deltaUpdate >= 1) {
				long diffTimeMillis = now - updateTime;
				update(diffTimeMillis);
				updateTime = now;
				deltaUpdate--;
			}

			if (targetFps <= 0 || deltaFps >= 1) {
				render();
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
				guiRenderer.render(guiInstance);
				deltaFps--;
				// draw into OpenGL window
				Window.getInstance().render();
			}
			initialTime = now;
		}

		cleanUp();	
	}

	private void stop() {
		if(!isRunning)
			return;
		
		isRunning = false;
	}
	
	private void render() {
		renderingEngine.render();
	}
	
	private void update(long diffTimeMillis) {
		renderingEngine.update();
	}
	
	private void cleanUp() {
		renderingEngine.shutdown();
		guiRenderer.cleanup();
		Window.getInstance().dispose();
		glfwTerminate();
		System.exit(0);
	}
	
	private void getDeviceProperties(){
		System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION) + " bytes");
		System.out.println("Max Geometry Uniform Blocks: " + GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS+ " bytes");
		System.out.println("Max Geometry Shader Invocations: " + GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS + " bytes");
		System.out.println("Max Uniform Buffer Bindings: " + GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS + " bytes");
		System.out.println("Max Uniform Block Size: " + GL31.GL_MAX_UNIFORM_BLOCK_SIZE + " bytes");
		System.out.println("Max SSBO Block Size: " + GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE + " bytes");

		int[] work_grp_cnt = new int[3];

		GL31.glGetIntegeri_v(GL43.GL_MAX_COMPUTE_WORK_GROUP_COUNT, 0, work_grp_cnt);
		GL31.glGetIntegeri_v(GL43.GL_MAX_COMPUTE_WORK_GROUP_COUNT, 1, work_grp_cnt);
		GL31.glGetIntegeri_v(GL43.GL_MAX_COMPUTE_WORK_GROUP_COUNT, 2, work_grp_cnt);

		System.out.println("GL_MAX_COMPUTE_WORK_GROUP_COUNT 0 " + work_grp_cnt[0]);
		System.out.println("GL_MAX_COMPUTE_WORK_GROUP_COUNT 1 " + work_grp_cnt[1]);
		System.out.println("GL_MAX_COMPUTE_WORK_GROUP_COUNT 2 " + work_grp_cnt[2]);

		GL31.glGetIntegeri_v(GL43.GL_MAX_COMPUTE_WORK_GROUP_SIZE, 0, work_grp_cnt);
		GL31.glGetIntegeri_v(GL43.GL_MAX_COMPUTE_WORK_GROUP_SIZE, 1, work_grp_cnt);
		GL31.glGetIntegeri_v(GL43.GL_MAX_COMPUTE_WORK_GROUP_SIZE, 2, work_grp_cnt);
		System.out.println("GL_MAX_COMPUTE_WORK_GROUP_SIZE 0 " + work_grp_cnt[0]);
		System.out.println("GL_MAX_COMPUTE_WORK_GROUP_SIZE 1 " + work_grp_cnt[1]);
		System.out.println("GL_MAX_COMPUTE_WORK_GROUP_SIZE 2 " + work_grp_cnt[2]);


		System.out.println("GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS " + GL11.glGetInteger(GL43.GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS));
	}
}
