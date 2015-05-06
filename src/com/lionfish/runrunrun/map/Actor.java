package com.lionfish.runrunrun.map;

import com.lionfish.runrunrun.gamelogic.Constants;
import com.lionfish.runrunrun.gamelogic.Game;
import com.lionfish.runrunrun.main.Assets;

public class Actor extends Entity {
	
	public enum Behavior {
		Walker, Bomber
	}

	protected boolean groundContact = false;
	protected boolean walkLeft = false;
	protected boolean walkRight = false;
	protected String anim = "empty_run";
	protected String baseAnim = "empty";
	protected float speed;
	protected Behavior behavior = Behavior.Walker;
	protected int lives;	

	public Actor(Game game, String baseAnim) {
		super(game);
		r = 0.0f;
		this.ay = 0.98f;
		this.baseAnim = baseAnim;
		this.anim = baseAnim + "_run";
		this.lives = 1;
	}
	
	@Override
	public void update(float t) {
		if(walkRight /*&& groundContact*/) {
			this.vx = speed;
		}
		if(walkLeft /*&& groundContact*/) {
			this.vx = - speed;
		}
		
		super.update(t);
		
		
		float sprW = Assets.anim(baseAnim + "_run", this).getWidth(null) * scaleX;
		
		
		float cx = x + sprW / 2f;
		float sampleX = cx / game.getCam().getViewPortW() *  Constants.MAP_GROUNDSAMPLES + 1;
		float sampleY = game.getMap().getGroundSample((int) sampleX);
		float sampleY2 = game.getMap().getGroundSample((int) sampleX + 1);

		float d = sampleY - sampleY2;
		
		
		float ny = game.getCam().getViewPortH() - sampleY * game.getCam().getViewPortH() - Assets.anim(baseAnim + "_run", this).getHeight(null) * scaleY;
		if(y > ny && ! dying) {
			if(behavior.equals(Behavior.Bomber)) {
				if(vy > 0) {
					game.startExpl(this.x, this.y);
					this.x = -10000;
				}
			} else {
				y = ny;
				groundContact = true;
				anim = baseAnim + "_run";
				this.vy = 0;
			}
		}
		this.r = (float) ((float) Math.atan(d) / Math.PI * 180f);
		
		if(vx != 0 && groundContact) {
			vx *= 0.9f;
			if(Math.abs(vx) < 0.01) {
				vx = 0;
			}
		}
		
		if(behavior.equals(Behavior.Bomber) && vy > 0) {
			r = (float) Math.PI;
		}
		
	}

	public void move(float x, float y) {
		this.x += x;
		this.y += y;
	}

	public void jump(boolean v) {
		if(v) {
			if(groundContact) {
				this.vy -= 20f;
				groundContact = false;
				anim = baseAnim + "_jump";
			}
		} else {
			if(this.vy < 0) {
				this.vy = 0;
			}
		}
	}

	public void walkLeft(boolean v) {
		walkLeft = v;
	}
	
	public void walkRight(boolean v) {
		walkRight = v;
	}

	public String getAnim() {
		return anim;
	}

	public void setSpeed(float d) {
		speed = d;
	}
	
	public boolean hasGroundContact() {
		return groundContact;
	}

	public Behavior getBehavior() {
		return behavior;
	}

	public void setBehavior(Behavior behavior) {
		this.behavior = behavior;
	}
	
	public void removeLife() {
		this.lives--;
		if (lives < 1) {
			setDying(true);
		}
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}
}
