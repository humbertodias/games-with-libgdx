package com.asteroids.gamestates;

import com.asteroids.game.GameMain;
import com.asteroids.managers.GameStateManager;
import com.asteroids.managers.Save;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class HighScoreState extends State{

	private SpriteBatch sb;
	private BitmapFont font;
	
	private long[] highScores;
	private String[] names;
	
	public HighScoreState(GameStateManager gsm) {
		super(gsm);
	}

	@Override
	public void init() {
		
		sb = new SpriteBatch();
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace_Bold.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 20;
		// adds spacing between the letters so they dont overlap!
		parameter.borderWidth = 1;
		
		font = gen.generateFont(parameter);
		gen.dispose();
		
		highScores = Save.gd.getHighScores();
		names = Save.gd.getNames();
		
		Save.load();
	}

	@Override
	public void update(float dt) {
		handleInput();
	}

	@Override
	public void render() {
		// ???? what DOES IT MEAN!
		sb.setProjectionMatrix(GameMain.cam.combined);
		
		sb.begin();

		String s; 
		float w; 
		
		// high scores table title
		s = "High Scores";
		w = font.getBounds(s).width;
		font.draw(sb, s, (GameMain.WIDTH - w) / 2, 300);
		
		// draw the high scores table
		for(int i = 0; i < highScores.length; i++) {
			s = String.format("%2d. %7s %s", i + 1, highScores[i], names[i]);
			w = font.getBounds(s).width;
			font.draw(sb, s, (GameMain.WIDTH - w) / 2, 270 - 20 * i);
		}
		
		sb.end();
		
	}

	@Override
	public void handleInput() {
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			gsm.setState(GameStateManager.menu);
		}
	}

	@Override
	public void dispose() {	
		sb.dispose();
		font.dispose();
	}

	

}
