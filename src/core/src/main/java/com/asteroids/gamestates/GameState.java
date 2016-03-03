package com.asteroids.gamestates;

import java.util.ArrayList;

import com.asteroids.entities.Asteroid;
import com.asteroids.entities.Bullet;
import com.asteroids.entities.FlyingSaucer;
import com.asteroids.entities.Particle;
import com.asteroids.entities.Player;
import com.asteroids.game.GameMain;
import com.asteroids.managers.GameStateManager;
import com.asteroids.managers.Jukebox;
import com.asteroids.managers.Save;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class GameState extends State {	
	// spritebatch is used for rendering font images etc etc
	private SpriteBatch sb;
	private ShapeRenderer sr;
	
	private BitmapFont font;
	private Player hudPlayer;
	
	private Player player;
	private ArrayList<Bullet> bullets;
	private ArrayList<Asteroid> asteroids;
	private ArrayList<Bullet> enemyBullets;
	
	private FlyingSaucer flyingSaucer;
	private float fsTimer;
	private float fsTime;
	
	private ArrayList<Particle> particles;
	
	private int level;
	private int totalAsteroids;
	private int numAsteroidsLeft;
	
	//background timing jazz
	private float maxDelay;
	private float minDelay;
	private float currentDelay;
	private float bgTimer;
	private boolean playLowPulse;
	
	public GameState(GameStateManager gsm) {
		super(gsm);
		
	}

	@Override
	public void init() {
		sr = new ShapeRenderer();
		sb = new SpriteBatch();
		
		// set font 
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace_Bold.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 20;
		font = generator.generateFont(parameter); // font size 12 pixels
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
		
		bullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		player = new Player(bullets);
		asteroids = new ArrayList<Asteroid>();

		particles = new ArrayList<Particle>();
		
		level = 1;
		spawnasteroids();
		
		hudPlayer = new Player(null);
		
		fsTimer = 0;
		fsTime = 15;
		
		// setup bg music
		maxDelay = 1;
		minDelay = 0.25f;
		currentDelay = maxDelay;
		bgTimer = maxDelay;
		playLowPulse = true;
	}
	
	private void createParticles(float x, float y) {
		for(int i = 0; i < 6; i++) {
			particles.add(new Particle(x, y));
		}
	}
	
	private void spawnasteroids() {
		asteroids.clear();
		
		int numToSpawn = 4 + level - 1;
		// 7, 1 large one 2 medium ones and 4 small ones
		totalAsteroids = numToSpawn * 7;
		numAsteroidsLeft = totalAsteroids;
		// reset bg music speed whenever we start a new level
		currentDelay = maxDelay;
		
		for (int i = 0; i < numToSpawn; i++) {

			float x = MathUtils.random(GameMain.WIDTH);
			float y = MathUtils.random(GameMain.HEIGHT);
			
			float dx = x - player.getx();
			float dy = y - player.gety();
			float dist = (float) Math.sqrt(dx * dx + dy * dy);
			
			//if the asteroid is within 100 pixels of the player DON'T spawn it there
			while (dist < 100) {
				x = MathUtils.random(GameMain.WIDTH);
				y = MathUtils.random(GameMain.HEIGHT);
				dx = x - player.getx();
				dy = y - player.gety();
			    dist = (float) Math.sqrt(dx * dx + dy * dy);
			}
			
			asteroids.add(new Asteroid(x, y, Asteroid.LARGE));
		
		}
		
	}
	 
	private void splitasteroids(Asteroid a) {
		createParticles(a.getx(), a.gety());
		// we just killed an asteroid
		numAsteroidsLeft--;
		// bg music speed
		currentDelay = ((maxDelay - minDelay) * numAsteroidsLeft / totalAsteroids) + minDelay;
		
		if(a.getType() == Asteroid.LARGE) {
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.MEDIUM));
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.MEDIUM));
		}
		if (a.getType() == Asteroid.MEDIUM) {
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.SMALL));
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.SMALL));
		}
			
	}

	@Override
	public void update(float dt) {
		// get user input
		handleInput();
		
		// next level
		if(asteroids.size() == 0) {
			level++;
			spawnasteroids();
		}
		
		// update player
		player.update(dt);
		if(player.isDead()) {
			if(player.getLives() == 0) {
				Jukebox.stopAll();
				Save.gd.setTentitiveScore(player.getScore());
				gsm.setState(GameStateManager.gameOver);
				return;
			}
			player.reset();
			player.loseLife();
			// when the player dies the flying saucer is destroyed
			flyingSaucer = null;
			Jukebox.stop("smallsaucer");
			Jukebox.stop("largesaucer");
			return;
		}
		
		// update player bullets
		for(int i = 0; i < bullets.size(); i++){
			bullets.get(i).update(dt);
			if(bullets.get(i).shouldRemove()){
				bullets.remove(i);
				i--;
			}
		}
		
		//update flying saucer
		if (flyingSaucer == null) {
			fsTimer += dt;
			if (fsTimer >= fsTime) {
				fsTimer = 0;
				int type = MathUtils.random() < 0.5 ?
						FlyingSaucer.SMALL : FlyingSaucer.LARGE;
				int direction = MathUtils.random() < 0.5 ?
						FlyingSaucer.RIGHT : FlyingSaucer.LEFT;
				
				flyingSaucer = new FlyingSaucer( type, direction, player, enemyBullets);
			}
		}
		// if there is a flying saucer already
		else {
			flyingSaucer.update(dt);
			if (flyingSaucer.shouldRemove()) {
				flyingSaucer = null;
				Jukebox.stop("smallsaucer");
				Jukebox.stop("largesaucer");
			}
		}
		
		// update flying saucer bullets
		for(int i = 0; i < enemyBullets.size(); i++){
			enemyBullets.get(i).update(dt);
			if(enemyBullets.get(i).shouldRemove()){
				enemyBullets.remove(i);
				i--;
			}
		}
		
		//update asteroids
		for(int i = 0; i < asteroids.size(); i++){
			asteroids.get(i).update(dt);
			if(asteroids.get(i).shouldRemove()){
				asteroids.remove(i);
				i--;
			}
		}
		
		// update particles
		for(int i = 0; i < particles.size(); i++){
			particles.get(i).update(dt);
			if(particles.get(i).shouldRemove()){
				particles.remove(i);
				i--;
			}
		}
		
		//check collisions
		checkCollisions();
		
		// play bg music
		bgTimer += dt;
		if (!player.isHit() && bgTimer >= currentDelay) {
			if(playLowPulse) {
				Jukebox.play("pulselow");
			} else {
				Jukebox.play("pulsehigh");
			}
			playLowPulse = !playLowPulse;
			bgTimer = 0;
		}
	}

	@Override
	public void render() {
		// ??????
		sb.setProjectionMatrix(GameMain.cam.combined);
		sr.setProjectionMatrix(GameMain.cam.combined);
		
		// draw player
		player.render(sr);
		
		// draw bullets
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(sr);
		}
		
		// draw flying saucer
		if (flyingSaucer != null) {
			flyingSaucer.render(sr);
		}
		
		// draw flying saucer bullets
		for (int i = 0; i < enemyBullets.size(); i++) {
			enemyBullets.get(i).render(sr);
		}
		
		// draw asteroids
		for (int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).render(sr);
		}
		
		// draw particles
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).render(sr);
		}
		
		String s = "Level: " + Long.toString(level);
		float w = font.getBounds(s).width;
		
		// draw score and level 
		sb.setColor(1,1,1,1);
		sb.begin();
		font.draw(sb, Long.toString(player.getScore()), 40, 390);
		font.draw(sb, s, GameMain.WIDTH - w, 390);
		
		sb.end();
		
		// draw lives
		for(int i = 0; i < player.getLives(); i++) {
			hudPlayer.setPosition(40 + i * 10, 360);
			hudPlayer.render(sr);
		}
	}

	@Override
	public void handleInput() {
		// we do is hit because otherwise we can shoot whilst dead
		if(!player.isHit()) {
			player.setLeft(Gdx.input.isKeyPressed(Keys.LEFT));
			player.setRight(Gdx.input.isKeyPressed(Keys.RIGHT));
			player.setUp(Gdx.input.isKeyPressed(Keys.UP));
		
			if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
				player.shoot();
			}
		}
	}
	
	// not a good way to check collisions, it can get messy VERY quickly
	private void checkCollisions() {
		
		//player-asteroid collision
		for(int i = 0; i < asteroids.size();i++) {
			Asteroid a = asteroids.get(i); 
			//if player intersects asteroid
			if(a.intersects(player)) {
				player.hit();
				asteroids.remove(i);
				i--;
				splitasteroids(a);
				Jukebox.play("explode");
				break;
			}
		}
		
		//bullet-asteroid collision
		for(int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			for(int j = 0; j < asteroids.size(); j++) {
				Asteroid a = asteroids.get(j);
				//if asteroid contains the point b
				if(a.contains(b.getx(), b.gety())){
					bullets.remove(i);
					i--;
					asteroids.remove(j); 
					j--;
					splitasteroids(a);
					// increment player score
					player.increamentScore(a.getScore());
					Jukebox.play("explode");
					break;
				}
			}
		}
		
		// player-flying saucer 
		if (flyingSaucer != null) {
			if(player.intersects(flyingSaucer)) {
				player.hit();
				createParticles(player.getx(), player.gety());
				createParticles(flyingSaucer.getx(), flyingSaucer.gety());
				flyingSaucer = null;
				Jukebox.stop("largesaucer");
				Jukebox.stop("smallsaucer");
				Jukebox.play("explode");
			}
		}

		// bullet flying suacer
		if (flyingSaucer != null) {
			for(int i = 0; i < bullets.size();i++) {
				Bullet b = bullets.get(i);
				if (flyingSaucer.contains(b.getx(), b.gety())) {
					bullets.remove(i);
					i--;
					createParticles(flyingSaucer.getx(), flyingSaucer.gety());
					player.increamentScore(flyingSaucer.getScore());
					flyingSaucer = null;
					Jukebox.stop("largesaucer");
					Jukebox.stop("smallsaucer");
					Jukebox.play("explode");
					break;
				}
			}
		}
		
		// player and enemy bullets collision
		if (!player.isHit()) {
			for (int i = 0; i < enemyBullets.size(); i++) {
				Bullet b = enemyBullets.get(i);
				if (player.contains(b.getx(), b.gety())) {
					player.hit();
					enemyBullets.remove(i);
					i--;
					Jukebox.play("explode");
					break;
				}
			}
		}
		
		// flying saucer and asteroid collision
		if (flyingSaucer != null) { 
			for (int i = 0; i < asteroids.size(); i++) {
				Asteroid a = asteroids.get(i);
				if (a.intersects(flyingSaucer)) {
					asteroids.remove(i);
					i--;
					splitasteroids(a);
					createParticles(a.getx(), a.gety());
					createParticles(flyingSaucer.getx(), flyingSaucer.gety());
					flyingSaucer = null;
					Jukebox.stop("largesaucer");
					Jukebox.stop("smallsaucer");
					Jukebox.play("explode");
					break;
				}
			}
		}
		
		// asteroid-enemy bullet collision
		for(int i = 0; i < enemyBullets.size(); i++) {
			Bullet b = enemyBullets.get(i);
			for (int j = 0; j < asteroids.size(); j++) {
				Asteroid a = asteroids.get(j);
				if (a.contains(b.getx(), b.gety())) {
					asteroids.remove(j);
					j--;
					splitasteroids(a);
					enemyBullets.remove(i);
					i--;
					createParticles(a.getx(), a.gety());
					Jukebox.play("explode");
					break;
				}
				
			}
		}
		
	}

	@Override
	public void dispose() {
		sb.dispose();
		sr.dispose();
		font.dispose();
	}

}
