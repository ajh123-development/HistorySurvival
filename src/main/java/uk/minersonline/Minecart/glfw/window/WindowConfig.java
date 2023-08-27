package uk.minersonline.Minecart.glfw.window;

public class WindowConfig {
	private final int defaultWidth;
	private final int defaultHeight;
	private final int minWidth;
	private final int minHeight;
	private final String title;
	private final int targetFps;
	private final int targetUps;


	public WindowConfig(int defaultWidth, int defaultHeight, int minWidth, int minHeight, String title, int targetFps, int targetUps) {
		this.defaultWidth = defaultWidth;
		this.defaultHeight = defaultHeight;
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		this.title = title;
		this.targetFps = targetFps;
		this.targetUps = targetUps;
	}

	public int getDefaultWidth() {
		return defaultWidth;
	}

	public int getDefaultHeight() {
		return defaultHeight;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public String getTitle() {
		return title;
	}

	public int getTargetFps() {
		return targetFps;
	}

	public int getTargetUps() {
		return targetUps;
	}
}
