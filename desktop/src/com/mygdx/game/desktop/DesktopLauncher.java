package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.MainGame;

public class DesktopLauncher {

	private static final String GAME_TITLE = "FlatWorld";
	private static final int WIDTH = 960;
	private static final int HEIGHT = 640;

	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(WIDTH, HEIGHT);
		config.setTitle(GAME_TITLE);

		new Lwjgl3Application(new MainGame(WIDTH, HEIGHT), config);
	}
}
