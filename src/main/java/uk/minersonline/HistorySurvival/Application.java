package uk.minersonline.HistorySurvival;

import uk.minersonline.Minecart.core.kernel.WindowConfig;

public class Application {
	private static final WindowConfig CONFIG = new WindowConfig(
		800,
		600,
		400,
		600,
		"History Survival",
		60,
		30
	);

	public static void main(String[] args) {
		Game game = new Game();
		game.getEngine().createWindow(CONFIG, new GuiImplementer());
		game.init();
		game.launch();
	}
}
