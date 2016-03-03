package com.asteroids.game;

import com.asteroids.managers.GameStateManager;
import com.asteroids.managers.Jukebox;
import com.asteroids.managers.Save;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameMain extends ApplicationAdapter {

	public static int WIDTH;
	public static int HEIGHT;
	public static boolean isRainbow = false;

	//OrthographicCamera is a designed camera for 2D games
	public static OrthographicCamera cam;
	
	private GameStateManager gsm;
	
	@Override
	public void create () {

		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
	
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();
		// if the high scores .sav file doesn't exist, create it!
		if(!Save.saveFileExists()){
			Save.init();
		}
		Save.load();
		
		// init sounds
		Jukebox.load("sounds/explode.ogg", "explode");
		Jukebox.load("sounds/extralife.ogg", "extralife");
		Jukebox.load("sounds/largesaucer.ogg", "largesaucer");
		Jukebox.load("sounds/pulsehigh.ogg", "pulsehigh");
		Jukebox.load("sounds/pulselow.ogg", "pulselow");
		Jukebox.load("sounds/saucershoot.ogg", "saucershoot");
		Jukebox.load("sounds/shoot.ogg", "shoot");
		Jukebox.load("sounds/smallsaucer.ogg", "smallsaucer");
		Jukebox.load("sounds/thruster.ogg", "thruster");
		
		gsm = new GameStateManager();
	}
	

	@Override
	public void render () {
		//clear screen to black
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render();
	}
}
