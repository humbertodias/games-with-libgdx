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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GameOverState extends State {

	private SpriteBatch sb;
	private ShapeRenderer sr;
	
	// is the score a new high score
	private boolean newHighScore;
	private char[] newName;
	private int currentChar;
	
	private BitmapFont font;
	private BitmapFont gameOverFont;
	
	public GameOverState(GameStateManager gsm) {
		super(gsm);
	}

	@Override
	public void init() {
		sb = new SpriteBatch();
		sr = new ShapeRenderer();
		
		newHighScore = Save.gd.isHighScore(Save.gd.getTentitiveScore());
		if(newHighScore) {
			newName = new char[] {'A', 'A', 'A' };
			currentChar = 0;
		}
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace_Bold.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 32;
		gameOverFont = gen.generateFont(parameter);
		parameter.size = 20;
		font = gen.generateFont(parameter);
	}

	@Override
	public void update(float dt) {
		handleInput();
	}

	@Override
	public void render() {
		sb.setProjectionMatrix(GameMain.cam.combined);
		
		sb.begin();
		
		String s;
		float w;
		
		s = "Game Over";
		w = gameOverFont.getBounds(s).width;
		gameOverFont.draw(sb, s, (GameMain.WIDTH - w) / 2, 220);
		
		// if we don't get a new high score then we just show the dead screen 
		if(!newHighScore) {
			s = "Press enter to lose";
			w = font.getBounds(s).width;
			font.draw(sb, s, (GameMain.WIDTH - w) / 2, 30);
			
			sb.end();
			return;
		}

		s = "New High Score: " + Save.gd.getTentitiveScore();
		w = font.getBounds(s).width;
		font.draw(sb, s, (GameMain.WIDTH - w) / 2, 180);
		
		// draw characters
		for(int i = 0; i < newName.length; i++) {
			font.draw(sb , Character.toString(newName[i]), 230 + 14 * i, 120);
		}
		
		s = "Press enter to lose";
		w = font.getBounds(s).width;
		font.draw(sb, s, (GameMain.WIDTH - w) / 2, 30);

		sb.end();
		
		// the under liner of the current character 
		sr.begin(ShapeType.Line); 
			sr.line( 
					230 + 13 * currentChar, 
					100, 
					244 + 14 * currentChar,
					100
				);
		sr.end();
	}

	@Override
	public void handleInput() {
		
		if(Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			if(newHighScore) {
				Save.gd.addHighScore(Save.gd.getTentitiveScore(), new String(newName));
				Save.save();
			}
			gsm.setState(GameStateManager.menu);
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.UP)) {
			if(newName[currentChar] == ' ') {
				newName[currentChar] = 'Z';
			} else {
				newName[currentChar]--;
				if(newName[currentChar] < 'A') {
					newName[currentChar] = ' ';
				}
			}
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.DOWN)) { 
			if(newName[currentChar] == ' ') {
				newName[currentChar] = 'A';
			} else {
				newName[currentChar]++;
				if(newName[currentChar] > 'Z') {
					newName[currentChar] = ' ';
				}
			}
		}
		
		
		if(Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
			if(currentChar < newName.length - 1) {
				currentChar++;
			}
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.LEFT)) {
			if(currentChar > 0) {
				currentChar--;
			}
		}
		
	}

	@Override
	public void dispose() { 
		sb.dispose();
		sr.dispose();
		font.dispose();
		gameOverFont.dispose();
	}


}
