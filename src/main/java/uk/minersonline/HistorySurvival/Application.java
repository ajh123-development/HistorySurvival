package uk.minersonline.HistorySurvival;

public class Application {
	public static void main(String[] args) {
		Game game = new Game();
		game.getEngine().createWindow(800, 600, "History Survival", new GuiImplementer());
		game.init();
		game.launch();
	}
}
