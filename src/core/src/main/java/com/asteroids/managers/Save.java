package com.asteroids.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.Gdx;

public class Save {

	public static GameData gd;
	
	public static void init() {
		gd = new GameData();
		gd.init();
		save();
		System.out.println("init!");
	}

	public static void save() {
		System.out.println("save!");
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("highscores.sav"));
			
			out.writeObject(gd);
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			Gdx.app.exit();
		}
		
	}
	
	public static void load() {
		System.out.println("load!");
		try {
			if(!saveFileExists()) {
				init();
			}

			ObjectInputStream in = new ObjectInputStream(new FileInputStream("highscores.sav"));
			
			gd = (GameData) in.readObject();
			in.close();
			
		} catch (Exception e) {
			e.printStackTrace(); 
			Gdx.app.exit();
		}
		
	}
	
	public static boolean saveFileExists() {
		File f = new File("highscores.sav");
		return f.exists();
	}
	
}
