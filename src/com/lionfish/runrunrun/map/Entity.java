package com.lionfish.runrunrun.map;

import com.lionfish.runrunrun.gamelogic.Game;

public class Entity {

	protected float x;
	protected float y;
	protected float vx;
	protected float vy;
	protected float ax;
	protected float ay;
	protected float scaleX = 1f;
	protected float scaleY = 1f;
	protected Game game;
	protected float r;
	protected boolean dying = false;
	protected boolean flipped = false;
	private int scoreValue;

	public Entity() {
		super();
	}

	public Entity(Game game) {
		this.game = game;
	}

	public void update(float t) {
		x += vx * t * 50;
		y += vy * t * 50;
		
		if(ax != 0) {
			vx += ax;
		}
		
		if(ay != 0) {
			vy += ay;
		}
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}

	public float getScaleX() {
		return scaleX;
	} 

	public float getScaleY() {
		return scaleY;
	}
	
	public float getR() {
		return r;
	}

	public void setDying(boolean b) {
		dying = b;
		if(b) {
			vy = -10;
		}
	}

	public float getVy() {
		return vy;
	}

	public boolean isFlipped() {
		return flipped;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}

	public boolean isDying() {
		return dying;
	}

	public int scoreValue() {
		return scoreValue;
	}

	public void setScoreValue(int scoreValue) {
		this.scoreValue = scoreValue;
	}
	
	public void setAcc(float ax, float ay) {
		this.ax = ax;
		this.ay = ay;
	}

	public void setScale(float x, float y) {
		scaleX = x;
		scaleY = y;
	}

	public float getVx() {
		return vx;
	}

	public void setVx(float vx) {
		this.vx = vx;
	}

	public void setVy(float vy) {
		this.vy = vy;
	}

	public float getAx() {
		return ax;
	}

	public void setAx(float ax) {
		this.ax = ax;
	}

	public float getAy() {
		return ay;
	}

	public void setAy(float ay) {
		this.ay = ay;
	}

}