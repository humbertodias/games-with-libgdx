package com.asteroids.gamestates;

import java.util.ArrayList;

import com.asteroids.entities.Asteroid;
import com.asteroids.game.GameMain;
import com.asteroids.managers.GameStateManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class MenuState extends State{

	private SpriteBatch sb;
	private ShapeRenderer sr;
	
	private BitmapFont titleFont;
	private BitmapFont font;
	
	private final String title = "Asteroids";
	
	private int currentItem;
	private String[] menuItems;
	private ArrayList<Asteroid> asteroids;
	
	public MenuState(GameStateManager gsm) {
		super(gsm);
	}

	@Override
	public void init() {
	 
		sb = new SpriteBatch();
		sr = new ShapeRenderer();
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace_Bold.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 56;
		parameter.color = Color.WHITE;
		
		titleFont = gen.generateFont(parameter); 
		parameter.size = 20;
		font = gen.generateFont(parameter);
		
		gen.dispose(); 
		
		menuItems = new String[] {
				"Play",
				"Highscores",
				"Quit",
				"Rainbow Mode!"
		};
		
		asteroids = new ArrayList<Asteroid>();
		for(int i = 0; i < 6; i++) {
			asteroids.add(new Asteroid(
					MathUtils.random(GameMain.WIDTH), 
					MathUtils.random(GameMain.HEIGHT), 
					Asteroid.LARGE)
					);
			
		}
		
	}

	@Override
	public void update(float dt) {
		handleInput();
		
		for (int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).update(dt);
		}
		
	}

	@Override
	public void render() {
		// ????
		sb.setProjectionMatrix(GameMain.cam.combined);
		sr.setProjectionMatrix(GameMain.cam.combined);
		
		// draw main menu asteroids
		for (int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).render(sr);
		}
		

		sb.begin();
		
		// draw title
		float width = titleFont.getBounds(title).width;
		titleFont.draw(sb, title, (GameMain.WIDTH - width) / 2 , 300);
		
		// draw menu
		for (int i = 0; i < menuItems.length; i++) {
			width = font.getBounds(menuItems[i]).width;
			if (currentItem == i) { 
				font.setColor(Color.RED);
			} else {
				font.setColor(Color.WHITE);
			}
			
			font.draw(sb, menuItems[i], (GameMain.WIDTH - width) / 2, 180 - 35 * i);
		}
			
		sb.end();
		
		
	}

	@Override
	public void handleInput() {
		
		if (Gdx.input.isKeyJustPressed(Keys.UP)) {
			if(currentItem > 0) {
				currentItem--;
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
			if(currentItem < menuItems.length - 1) {
				currentItem++;
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			select();
		}
	}
	
	private void select() {
		// play
		if (currentItem == 0) {
			gsm.setState(GameStateManager.play);
		} 
		// high scores
		if (currentItem == 1 ) {
			gsm.setState(GameStateManager.highscore);
		}
		// exit
		if (currentItem == 2) {
			Gdx.app.exit();
		}
		// ranbow mode
		if (currentItem == 3) {
			GameMain.isRainbow = !GameMain.isRainbow;
		}
	}

	@Override
	public void dispose() {
		sb.dispose();
		sr.dispose();
		titleFont.dispose();
		font.dispose();
		
	}

	
	
	
	
	
}
