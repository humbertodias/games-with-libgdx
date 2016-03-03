package com.asteroids.game.desktop;

import com.asteroids.game.GameMain;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Asteroids";
		config.width = 500;
		config.height = 400;
		config.resizable = false;
		
		new LwjglApplication(new GameMain(), config);
	}
}
