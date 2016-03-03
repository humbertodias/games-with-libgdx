package com.asteroids.gamestates;

import com.asteroids.managers.GameStateManager;

public abstract class State {
	// once finished tutorial compress the state files
	public GameStateManager gsm;

	public State (GameStateManager gsm){
		this.gsm = gsm;
		init();
	}
	
	public abstract void init();
	public abstract void update (float dt);
	public abstract void render();
	public abstract void handleInput();
	public abstract void dispose();
	
}
