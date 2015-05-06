package com.lionfish.runrunrun.map;

import java.awt.Image;

import com.lionfish.runrunrun.gamelogic.Game;
import com.lionfish.runrunrun.main.Assets;

public class Doodad extends Entity {
	public enum DoodadType{CLOUD, GRASS, FLOWER, ROCK, TREE, SUN, DIRT, CARROTS, MOON, ICICLE, CACTUS, EXPLOSION, LIFE, XX};
	
	private DoodadType type;
	private int variation = 1;
	private boolean isPickup = false;




	public Doodad(DoodadType type, Game game, float x, float y, float vx, float vy, float scaleX, float scaleY) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.game = game;
	}
	
	
	public Image getGfx() {
		switch(getType()) {
		case LIFE:
			return Assets.GFX_LIFE;
		case CLOUD:
			return Assets.GFX_CLOUD;
		case CARROTS:
			return Assets.GFX_CARROTS;
		case SUN:
			return Assets.GFX_SUN;
		case MOON:
			return Assets.GFX_MOON;
		case EXPLOSION:
			return Assets.anim("expl", this);
		case FLOWER:
			switch (variation) {
			case 1:
				return Assets.GFX_FLOWER1;
			case 2:
				return Assets.GFX_FLOWER2;
			case 3:
				return Assets.GFX_FLOWER3;
			}
		case XX:
			switch (variation) {
			case 1:
				return Assets.GFX_X2;
			case 2:
				return Assets.GFX_X3;
			case 3:
				return Assets.GFX_X4;
			}
		case ICICLE:
			switch (variation) {
			case 1:
				return Assets.GFX_ICICLE1;
			case 2:
				return Assets.GFX_ICICLE2;
			case 3:
				return Assets.GFX_ICICLE3;
			}
		case CACTUS:
			switch (variation) {
			case 1:
				return Assets.GFX_CACTUS1;
			case 2:
				return Assets.GFX_CACTUS2;
			case 3:
				return Assets.GFX_CACTUS3;
			}
		case ROCK:
			switch (variation) {
			case 1:
				return Assets.GFX_ROCK1;
			case 2:
				return Assets.GFX_ROCK2;
			case 3:
				return Assets.GFX_ROCK3;
			}
		case DIRT:
			switch (variation) {
			case 1:
				return Assets.GFX_DIRT1;
			case 2:
				return Assets.GFX_DIRT2;
			case 3:
				return Assets.GFX_DIRT3;
			}
		default: 
			return null;
		}
	}
	
	public DoodadType getType() {
		return type;
	}
	public void setType(DoodadType type) {
		this.type = type;
	}
	
	public void setVariation(int variation) {
		this.variation = variation;
	}


	public boolean isPickup() {
		return isPickup;
	}
	
	public void setPickup(boolean isPickup) {
		this.isPickup = isPickup;
	}


	public float getVx() {
		return this.vx;
	}
 
}
