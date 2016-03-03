package com.asteroids.managers;

import com.asteroids.gamestates.GameOverState;
import com.asteroids.gamestates.GameState;
import com.asteroids.gamestates.HighScoreState;
import com.asteroids.gamestates.MenuState;
import com.asteroids.gamestates.State;

public class GameStateManager {

	// current game state
	public State currentState;
	
	public static final int menu = 0;
	public static final int play = 1;
	public static final int highscore = 2;
	public static final int gameOver = 3;

	public GameStateManager() {
		setState(menu);
	}

	public void setState(int state) {	
		if(currentState != null) currentState.dispose();
		
		if (state == menu) {currentState = new MenuState(this);}
		if (state == play) {currentState = new GameState(this);}
		if (state == highscore) {currentState = new HighScoreState(this);}
		if (state == gameOver) {currentState = new GameOverState(this);}
		
	}

	public void update(float dt) {
		currentState.update(dt);
	}
	
	public void render(){
		currentState.render();
	}
}
