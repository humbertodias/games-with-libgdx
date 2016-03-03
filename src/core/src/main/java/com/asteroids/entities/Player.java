package com.asteroids.entities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.asteroids.game.GameMain;
import com.asteroids.managers.Jukebox;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

public class Player extends SpaceObject {

	private final int MAX_BULLETS = 4;
	private ArrayList<Bullet> bullets;
	
	private float[] flamex;
	private float[] flamey;
	
	private boolean left, right, up;

	private float maxSpeed;
	private float acceleration;
	private float deceleration;
	private float acceleratingTimer;
	
	private boolean hit;
	private boolean dead;
	
	private float hitTimer;
	private float hitTime;
	private Line2D.Float[] hitLines;
	private Point2D.Float[] hitLinesVector;
	
	private long score;
	private int extraLives;
	// the score you need to get another life
	private long requiredScore;

	public Player(ArrayList<Bullet> bullets) {
		
		this.bullets = bullets;
		
		x = GameMain.WIDTH / 2;
		y = GameMain.HEIGHT / 2;

		// all in pixels per second
		maxSpeed = 300;
		acceleration = 200;
		deceleration = 10;

		// the space ship has four points and each point has an x and y
		shapex = new float[4];
		shapey = new float[4];
		
		// the flame on the bottom of the ship when accelerating (triangle)
		flamex = new float[3];
		flamey = new float[3];
		
		//make the player face upwards
		radians = 3.1415f / 2;
		rotationSpeed = 3;
		
		hit = false;
		hitTimer = 0;
		hitTime = 2;

		score = 0;
		extraLives = 3;
		requiredScore = 10000;
	}
	
	private void setShape() {
		//Trigonometry! woot
		
		//top point (nose cone)
		shapex[0] = x + MathUtils.cos(radians) * 8;
		shapey[0] = y + MathUtils.sin(radians) * 8;

		//left point (left wing)
		shapex[1] = x + MathUtils.cos(radians - 4 * 3.1415f / 5) * 8;
		shapey[1] = y + MathUtils.sin(radians - 4 * 3.1415f / 5) * 8;
		
		//center bottom 
		shapex[2] = x + MathUtils.cos(radians + 3.1415f) * 5;
		shapey[2] = y + MathUtils.sin(radians + 3.1415f) * 5;
		
		//right point (right wing)
		shapex[3] = x + MathUtils.cos(radians + 4 * 3.1415f / 5) * 8;
		shapey[3] = y + MathUtils.sin(radians + 4 * 3.1415f / 5) * 8;
		
	}
	
	private void setFlame() {
		//left point
		flamex[0] = x + MathUtils.cos(radians - 5 * 3.1415f / 6) * 5;
		flamey[0] = y + MathUtils.sin(radians - 5 * 3.1415f / 6) * 5;
		
		//bottom point
		flamex[1] = x + MathUtils.cos(radians - 3.1415f) *
				(6 + acceleratingTimer * 50);
		flamey[1] = y + MathUtils.sin(radians - 3.1415f) *
				(6 + acceleratingTimer * 50);
		
		//right point
		flamex[2] = x + MathUtils.cos(radians + 5 * 3.1415f / 6) * 5;
		flamey[2] = y + MathUtils.sin(radians + 5 * 3.1415f / 6) * 5;
	}
	
	public void setLeft(boolean b) {left = b;}
	public void setRight(boolean b) {right = b;}
	public void setUp(boolean b) {
		if(b && !up && !hit) {
			Jukebox.loop("thruster");
		} else if (!b) {
			Jukebox.stop("thruster");
		}
		up = b;
	}
	
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		setShape();
	}
	
	public boolean isHit() {return hit;}
	public boolean isDead() {return dead;}
	public void reset() {
		x = GameMain.WIDTH / 2;
		y = GameMain.HEIGHT / 2;
		setShape();
		hit = dead = false;
	}
	
	public long getScore() {return score;}
	public int getLives() {return extraLives;}
	
	public void loseLife() {extraLives--;}
	public void increamentScore(long l) {score += l;}
	
	public void shoot() {
		if(bullets.size() == MAX_BULLETS) return;
		bullets.add(new Bullet(x, y, radians));
		Jukebox.play("shoot");
	}
	
	public void hit() {
		
		if(hit) return;
		
		hit = true;
		dx = dy = 0;
		left = right = up = false;
		Jukebox.stop("thruster");
		
		hitLines = new Line2D.Float[4];
		for (int i = 0, j = hitLines.length - 1;
				i < hitLines.length; 
				j = i++) {
			hitLines[i] = new Line2D.Float(shapex[i], shapey[i], shapex[j], shapey[j]);
		}
		
		hitLinesVector = new Point2D.Float[4];
		hitLinesVector[0] = new Point2D.Float(
				MathUtils.cos(radians + 1.5f), 
				MathUtils.sin(radians + 1.5f)
		);
		
		hitLinesVector[1] = new Point2D.Float(
				MathUtils.cos(radians - 1.5f), 
				MathUtils.sin(radians - 1.5f)
		);
		
		hitLinesVector[2] = new Point2D.Float(
				MathUtils.cos(radians - 2.8f), 
				MathUtils.sin(radians - 2.8f)
		);
		
		hitLinesVector[3] = new Point2D.Float(
				MathUtils.cos(radians + 2.8f), 
				MathUtils.sin(radians + 2.8f)
		);
		
	}
	
	public void update(float dt){
		
		// check if hit
		if(hit) {
			hitTimer += dt;
			if(hitTimer > hitTime) {
				dead = true;
				hitTimer = 0;
			}
			for(int i = 0; i < hitLines.length; i++) {
				hitLines[i].setLine(
						hitLines[i].x1 + hitLinesVector[i].x * 10 * dt,
						hitLines[i].y1 + hitLinesVector[i].y * 10 * dt,
						hitLines[i].x2 + hitLinesVector[i].x * 10 * dt,
						hitLines[i].y2 + hitLinesVector[i].y * 10 * dt
					);
			}
			return;
			
		}
		
		// check extra lives
		if (score >= requiredScore) {
			extraLives++;
			requiredScore += 10000;
			Jukebox.play("extralife");
		}
		
		// turning
		if(left) {radians += rotationSpeed * dt;}
		if(right) {radians -= rotationSpeed * dt;}	
		
		// acceleration
		if(up) {
			dx += MathUtils.cos(radians) * acceleration * dt;
			dy += MathUtils.sin(radians) * acceleration * dt; 
			
			acceleratingTimer += dt;
			if(acceleratingTimer > 0.1f){
				acceleratingTimer = 0;
			}
		} else {
			acceleratingTimer = 0;
		}
		
		// deceleration                   Pythagoras
		float vec = (float) Math.sqrt(dx * dx + dy * dy);
		if(vec > 0) {
			dx -= (dx / vec) * deceleration * dt;
			dy -= (dy / vec) * deceleration * dt;
		}
		if (vec > maxSpeed){
			dx = (dx / vec) * maxSpeed;
			dy = (dy / vec) * maxSpeed;
		}
		
		// set Postition
		x += dx * dt;
		y += dy * dt;
		
		// set shape
		setShape();
		
		// set flame
		if(up){
			setFlame();
		}
		
		// screen wrap
		wrap();
		
	}
	
	public void render(ShapeRenderer sr){
		
		sr.setColor(1, 1, 1, 1);
		
		// draw in between these two
		sr.begin(ShapeType.Line);
		
		// check if hit
		if(hit) {
			for(int i = 0; i < hitLines.length; i++) {
				sr.line(
					hitLines[i].x1,
					hitLines[i].y1,
					hitLines[i].x2,
					hitLines[i].y2
					);
			}
			// if we are hit then we don't want to render anything else
			sr.end();
			return;
		}
		
		// draw ship
			for(int i = 0, j = shapex.length - 1;
					i < shapex.length;
					j = i++){
				
				sr.line(shapex[i], shapey[i], shapex[j], shapey[j]);
			}
			
		// draw flame
			if(up){
				for(int i = 0, j = flamex.length - 1;
					i < flamex.length;
					j = i++){
				
					sr.line(flamex[i], flamey[i], flamex[j], flamey[j]);
				}
			}
			
		sr.end();
		
	}

}
