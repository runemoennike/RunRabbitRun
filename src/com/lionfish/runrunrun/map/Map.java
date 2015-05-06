package com.lionfish.runrunrun.map;

import java.util.Random;

import com.lionfish.runrunrun.gamelogic.Constants;
import com.lionfish.runrunrun.gamelogic.Game;

public class Map {
	
	private Game game;

	private float[] ground;
	private Random rnd;
	private int next;
	
	private float r1;
	private float r2;
	private float r3;
	private float time;

	private float scrollDelta;

	public Map(Game game) {
		this.game = game;
		rnd = new Random();
		next = -1;
		ground = new float[Constants.MAP_GROUNDSAMPLES];
		
		genRands();
		
		generate();
	}

	public void genRands() {
		r1 = rnd.nextFloat() * 2f + 12f;
		r2 = rnd.nextFloat() * 2f + 8f;
		r3 = rnd.nextFloat() + 6f;
	}

	public void generate() {
		for(int i = 0; i < ground.length; i ++) {
			ground[i] = getSample((i - Constants.MAP_GROUNDSAMPLES) / 5f);
		}
	}
	
	public float getSample(float x) {
		return (float) (
				(Math.cos(x / r1)) / 10f + 
				(Math.cos(x / r2) + 5f) / 10f +
				(Math.cos(x / r3) + 2f) / 10f
				- 0.4f
				);
	}
	
	
	public void generateNext() {
		next = (next + 1) % Constants.MAP_GROUNDSAMPLES;
		ground[next] = getSample(time);
	}

	public float getGroundSample(int x) {
		x += next + 1;
		x = x < Constants.MAP_GROUNDSAMPLES ? x : x - Constants.MAP_GROUNDSAMPLES; 
		
		if(x >= ground.length || x < 0) {
			return 1f;
		}
		return ground[x];
	}

	public void update(float t) {
		scrollDelta += (t * Constants.GROUNDSPEED * 50f);
		float threshold = (float) game.getCam().getViewPortW() / Constants.MAP_GROUNDSAMPLES;
		
		while(scrollDelta > threshold) {
			time += t * 10; 
			scrollDelta -= threshold;
			generateNext();
		}
	}	
	
	public float getScrollDelta() {
		return scrollDelta;
	}
}
