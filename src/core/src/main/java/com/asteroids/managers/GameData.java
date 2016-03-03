package com.asteroids.managers;

import java.io.Serializable;

public class GameData implements Serializable{
	private static final long serialVersionUID = 1;

	// top ten scores will be saved
	private final int MAX_SCORES = 10;
	private long[] highScores;
	private String[] names;
	
	// checking if the score made it into the high scores list
	private long tentitiveScore;
	
	public GameData() {
		highScores = new long[MAX_SCORES];
		names = new String[MAX_SCORES];
	}
	

	public void init() {
		
		// sets up an empty high scores table
		for(int i = 0; i < MAX_SCORES; i++) {
			highScores[i] = 0;
			// empty name
			names[i] = "---";
		}
		
	}
	
	public long[] getHighScores() {return highScores;}
	public String[] getNames() {return names;}
	
	public long getTentitiveScore() {return tentitiveScore;}
	public void setTentitiveScore(long i) { tentitiveScore = i;}
	
	public boolean isHighScore(long score) { 
		return score > highScores[MAX_SCORES - 1];
	}
	
	public void addHighScore(long newScore, String name) {
		// check if valid highscore
		if (isHighScore(newScore)) {
			highScores[MAX_SCORES - 1] = newScore;
			names[MAX_SCORES - 1] = name;
			sortHighScores();
		}
	}
	
	public void sortHighScores() {
		// weird version of the bubble sort
		for(int i = 0; i < MAX_SCORES; i++) {
			long score = highScores[i];
			String name = names[i];
			int j;
			for(j = i - 1; j >= 0 && highScores[j] < score; j--) {
				highScores[j + 1] = highScores[j];
				names[j + 1] = names[j];
			}
			highScores[j + 1] = score;
			names[j + 1] = name;
		}
	}
	
}
