package com.lionfish.runrunrun.map;

import com.lionfish.runrunrun.gamelogic.Game;
import com.lionfish.runrunrun.gfx.Renderer;
import com.lionfish.runrunrun.main.Assets;

public class Player extends Actor {

	private final static int maxComboBuff = 4;
	
	private float score;
	private int creeps;
	private int comboBuff;
	private int carrots;
	private double lastLifeLostTime;
	private int fbId;
	private String fbName = "";
	private String fbLink = "";

	public Player(Game game, String baseAnim) {
		super(game, baseAnim);
		this.lives = 3;
		this.comboBuff = 0;
	}

	@Override
	public void update(float t) {
		super.update(t);

		float sprW = Assets.anim(baseAnim + "_run", this).getWidth(null)
				* scaleX;

		x -= t * 100;

		if (x < 0) {
			x = 0;
		}
		if (x + sprW >= game.getCam().getViewPortW()) {
			x = game.getCam().getViewPortW() - sprW - 1;
		}

		if (hasGroundContact()) {
			comboBuff = 0;
		}
	}

	public void addPoints(float t) {
		score += t;
		if (t > 10) {
			Renderer.flashScore();
		}
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public void jumpOnBaddie(Actor b) {
		this.vy = -12f;
		comboBuff = comboBuff == maxComboBuff ? comboBuff : comboBuff + 1;
		addPoints(b.scoreValue() * comboBuff);
		creeps++;
	}

	public void setLastLifeLostTime(double gametime) {
		this.lastLifeLostTime = gametime;
	}

	public boolean isInvulnable(double gametime) {
		return gametime - this.lastLifeLostTime < 3.0;
	}

	public void setCreeps(int creeps) {
		this.creeps = creeps;
	}

	public int getCreeps() {
		return creeps;
	}

	public int getFbId() {
		return fbId;
	}

	public void setFbId(int fbId) {
		this.fbId = fbId;
	}

	public String getFbName() {
		return fbName;
	}

	public void setFbName(String fbName) {
		this.fbName = fbName;
	}

	public String getFbLink() {
		return fbLink;
	}

	public void setFbLink(String fbLink) {
		this.fbLink = fbLink;
	}

	public int getCarrots() {
		return carrots;
	}

	public void setCarrots(int carrots) {
		this.carrots = carrots;
	}
	
	public int getComboBuff() {
		return this.comboBuff;
	}
}
