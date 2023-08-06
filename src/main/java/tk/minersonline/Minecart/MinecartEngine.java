package tk.minersonline.Minecart;

import tk.minersonline.Minecart.glfw.window.Window;
import tk.minersonline.Minecart.glfw.window.WindowConfig;
import tk.minersonline.Minecart.gui.IGuiInstance;
import tk.minersonline.Minecart.scene.Scene;
import tk.minersonline.Minecart.glfw.Renderer;
import tk.minersonline.Minecart.scene.views.ProjectionHandler;

public class MinecartEngine {
	private final MinecartGame game;
	private final Window window;
	private final Renderer render;
	private boolean running;
	private final Scene scene;
	private final int targetFps;
	private final int targetUps;


	public MinecartEngine(WindowConfig config, MinecartGame game, ProjectionHandler projection) {
		this.window = new Window(config, () -> {
			resize();
			return null;
		});
		targetFps = config.getTargetFps();
		targetUps = config.getTargetUps();
		this.game = game;
		this.render = new Renderer(window);
		this.scene = new Scene(projection);
		game.init(window, scene, render);
		running = true;
	}

	private void cleanup() {
		game.cleanup();
		render.cleanup();
		scene.cleanup();
		window.cleanup();
	}

	private void resize() {
		int width = window.getWidth();
		int height = window.getHeight();
		scene.resize(width, height);
		render.resize(width, height);
	}

	private void run() {
		long initialTime = System.currentTimeMillis();
		float timeU = 1000.0f / targetUps;
		float timeR = targetFps > 0 ? 1000.0f / targetFps : 0;
		float deltaUpdate = 0;
		float deltaFps = 0;

		long updateTime = initialTime;
		IGuiInstance iGuiInstance = scene.getGuiInstance();
		while (running && !window.windowShouldClose()) {
			window.pollEvents();

			long now = System.currentTimeMillis();
			deltaUpdate += (now - initialTime) / timeU;
			deltaFps += (now - initialTime) / timeR;

			if (targetFps <= 0 || deltaFps >= 1) {
				window.getMouseListener().input();
				boolean inputConsumed = iGuiInstance != null && iGuiInstance.handleGuiInput(scene, window);
				game.input(window, scene, now - initialTime, inputConsumed);
			}

			if (deltaUpdate >= 1) {
				long diffTimeMillis = now - updateTime;
				game.update(window, scene, diffTimeMillis);
				updateTime = now;
				deltaUpdate--;
			}

			if (targetFps <= 0 || deltaFps >= 1) {
				render.render(window, scene);
				deltaFps--;
				window.update();
			}
			initialTime = now;
		}

		cleanup();
	}

	public void start() {
		running = true;
		run();
	}

	public void stop() {
		running = false;
	}
}
