package tk.minersonline.Minecart.glfw;

public class WindowConfig {
	private final int DEFAULT_WIDTH;
	private final int DEFAULT_HEIGHT;
	private final int MIN_WIDTH;
	private final int MIN_HEIGHT;
	private final String TITLE;

	public WindowConfig(int defaultWidth, int defaultHeight, int minWidth, int minHeight, String title) {
		DEFAULT_WIDTH = defaultWidth;
		DEFAULT_HEIGHT = defaultHeight;
		MIN_WIDTH = minWidth;
		MIN_HEIGHT = minHeight;
		TITLE = title;
	}

	public int getDefaultWidth() {
		return DEFAULT_WIDTH;
	}

	public int getDefaultHeight() {
		return DEFAULT_HEIGHT;
	}

	public int getMinWidth() {
		return MIN_WIDTH;
	}

	public int getMinHeight() {
		return MIN_HEIGHT;
	}

	public String getTitle() {
		return TITLE;
	}
}
