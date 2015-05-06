package com.lionfish.runrunrun.gamelogic;

import java.util.ArrayList;
import java.util.Iterator;

import com.lionfish.runrunrun.communication.Communicator;
import com.lionfish.runrunrun.gfx.Renderer;
import com.lionfish.runrunrun.gfx.Renderer.ColorScheme;
import com.lionfish.runrunrun.main.Assets;
import com.lionfish.runrunrun.main.Menu;
import com.lionfish.runrunrun.map.Actor;
import com.lionfish.runrunrun.map.Actor.Behavior;
import com.lionfish.runrunrun.map.Doodad;
import com.lionfish.runrunrun.map.Doodad.DoodadType;
import com.lionfish.runrunrun.map.Entity;
import com.lionfish.runrunrun.map.Map;
import com.lionfish.runrunrun.map.Player;

public class Game {
	public enum GameState {
		MENU, GAME
	};

	private Camera cam;
	private Map map;
	private Player pl;

	private ArrayList<Doodad> doodads = new ArrayList<Doodad>();
	private ArrayList<Doodad> doodadsBg = new ArrayList<Doodad>();
	private ArrayList<Actor> baddies = new ArrayList<Actor>();
	private Doodad sun;
	private GameState state;

	private double gameTime = 0;

	private int level = -1;
	private DoodadType decoDoodad;
	private Menu menu;
	private ArrayList<Doodad> lifeDoodads = new ArrayList<Doodad>();

	public Game() {
		cam = new Camera();
		pl = new Player(this, "bunny");
		pl.setScale(0.4f, 0.4f);
		pl.setSpeed(Constants.PLAYER_SPEED);
		menu = new Menu();
		state = GameState.MENU;
		Communicator.retrieveSalt();
	}

	public Camera getCam() {
		return cam;
	}

	public void update(float t) {
		this.cam.update(t);
		gameTime += t;

		map.update(t);

		spawnDoodads();

		for (Iterator<Doodad> it = doodads.iterator(); it.hasNext();) {
			Doodad d = it.next();
			d.update(t);
			if (d.getX() < -300 || d.getY() > cam.getViewPortH() * 2) {
				it.remove();
			}
		}

		for (Iterator<Doodad> it = doodadsBg.iterator(); it.hasNext();) {
			Doodad d = it.next();
			d.update(t);
			if (d.getX() < -500 || d.getY() > cam.getViewPortH() * 2 || d.getY() < -500) {
				it.remove();
			}
		}

		spawnBaddies();

		for (Iterator<Actor> it = baddies.iterator(); it.hasNext();) {
			Entity a = it.next();
			a.update(t);
			if (a.getX() < -300 || a.getX() > cam.getViewPortW() + 300  || a.getY() > cam.getViewPortH() * 2) {
				it.remove();
			}
		}

		switch (state) {
		case GAME:
			pl.addPoints(t * 10);

			pl.update(t);

			if (!pl.isDying()) {
				checkCollisions();
			}

			if (!pl.isInvulnable(this.gameTime)
					&& pl.getY()
							+ Assets.anim(pl.getAnim(), pl).getHeight(null)
							* pl.getScaleY() / 3 > cam.getViewPortH()
							* (1f - Constants.WATER_HEIGHT)) {

				playerHurt();
				if (pl.isDying()) {
					prepareDeathScreen("Rabbits do not swim!");
				}
			}
			break;
		case MENU:
			break;
		}

		// System.out.println(sun.getX());
		if (sun.getX() < -sun.getGfx().getWidth(null) * sun.getScaleX()) {
			nextMap();
		}

		// System.out.println(doodads.size());
	}

	private void checkCollisions() {
		//float plW = Assets.anim(pl.getAnim(), pl).getWidth(null);
		//float plH = Assets.anim(pl.getAnim(), pl).getHeight(null);
		
		for (Actor b : baddies) {
			if (b.isDying() || b.getBehavior().equals(Behavior.Bomber)) {
				continue;
			}

			float x1 = b.getX(); // + Assets.anim(b.getAnim(), b).getWidth(null) / 2f;
			float y1 = b.getY(); // + Assets.anim(b.getAnim(), b).getHeight(null) / 2f;
			float x2 = pl.getX(); // + plW / 2f;
			float y2 = pl.getY(); // + plH / 2f;
			
			double distSq = (x1-x2) * (x1-x2)
					+ (y1-y2) * (y1-y2);

			if (distSq < Constants.COLLISION_DIST_SQ) {
				if ((b.getY() - pl.getY() >= 50f) && !pl.hasGroundContact()) {
					b.removeLife();
					pl.jumpOnBaddie(b);
					int buff = pl.getComboBuff();
					if (buff > 1) {
						Doodad comboBuff = new Doodad(DoodadType.XX, this, x1, y1 - 50f, 0, -5f, buff / 2.0f, buff / 2.0f);
						comboBuff.setVariation(buff - 1);
						doodadsBg.add(comboBuff);
					}
				} else {
					if (!pl.isInvulnable(this.gameTime) && !b.isDying()) {
						playerHurt();
						if (pl.isDying()) {
							prepareDeathScreen("Ouch! That onion hurt!");
						}
					} else {
						b.setDying(true);
					}
				}
			}
		}

		for (Doodad d : doodadsBg) {
			if (d.isDying() || !d.isPickup()) {
				continue;
			}

			double distSq = (d.getX() - pl.getX()) * (d.getX() - pl.getX())
					+ (d.getY() - pl.getY()) * (d.getY() - pl.getY());

			if (distSq < Constants.COLLISION_DIST_SQ) {
				d.setDying(true);
				d.setAcc(0, -1);
				pl.addPoints(d.scoreValue());
				
				if(d.getType().equals(DoodadType.CARROTS)) {
					pl.setCarrots(pl.getCarrots() + 1);
				}
			}
		}

	}

	private void playerHurt() {
		pl.removeLife();
		pl.setLastLifeLostTime(this.gameTime);
//		pl.setX(100);
//		pl.setY(0);
		Renderer.flashPlayer();
		lifeDoodads.get(lifeDoodads.size() - 1).setAy(0.98f);
		lifeDoodads.remove(lifeDoodads.size() - 1);
	}

	private void prepareDeathScreen(String deathText) {
		menu.getItems().clear();
		menu.addItem("mainmenu", "[Enter] to continue");
		menu.setTopText(deathText);
		menu.setBotText("You scored " + String.format("%.2f", pl.getScore()) + " points!| ");
		if(pl.getFbId() != 0) {
			Communicator.submitScore(pl.getFbId(), pl.getFbName(), pl.getFbLink(), (int) pl.getScore(), (float) gameTime, pl.getCreeps());
			Communicator com = new Communicator();
			com.start();
		}
		state = GameState.MENU;
	}

	private void spawnBaddies() {
		if (gameTime > 5f) {
			if (Math.random() < 0.004) {
				Actor baddie = new Actor(this, "onion");
				baddie.setX(cam.getViewPortW() + 50);
				baddie.setSpeed((float) (Math.random() * 3f + 1f));
				baddie.walkLeft(true);
				// baddie.setScale(0.8f, 0.8f);
				baddie.setFlipped(Math.random() < 0.5f ? true : false);
				baddie.setScoreValue(Constants.VALUE_ONION);
				baddies.add(baddie);
			}

			if (Math.random() < 0.004) {
				Actor baddie = new Actor(this, "onion");
				baddie.setX(0 - 25);
				baddie.setSpeed((float) (Math.random() * 3f + 1f));
				baddie.walkRight(true);
				// baddie.setScale(0.8f, 0.8f);
				baddie.setFlipped(true);
				baddie.setScoreValue(Constants.VALUE_ONION);
				baddies.add(baddie);
			}

			if (Math.random() < 0.003) {
				Actor baddie = new Actor(this, "mole");
				baddie.setX((float) (Math.random() * cam.getViewPortW()));
				baddie.setY(cam.getViewPortH());
				baddie.setVy(-20);
				baddie.setAy(0.3f);
				// baddie.setVx((float) (Math.random() * 2f));
				baddie.setScale(0.5f, 0.5f);
				baddie.setScoreValue(Constants.VALUE_MOLE);
				baddie.setBehavior(Behavior.Bomber);
				baddies.add(baddie);
			}
		}
	}

	private void spawnDoodads() {
		if (Math.random() < 0.01) {
			Doodad d = new Doodad(DoodadType.CLOUD, this, cam.getViewPortW(),
					(float) (cam.getViewPortH() / 4 * Math.random()),
					(float) -Math.random() * 4f, 0,
					(float) Math.random() + 0.5f, (float) Math.random() + 0.5f);
			doodadsBg.add(d);
		}

		float stationarySpeed = -Constants.GROUNDSPEED;

		if (Math.random() < 0.15) {
			float scale = (float) Math.random();
			float y = cam.getViewPortH()
					- map.getGroundSample(Constants.MAP_GROUNDSAMPLES - 1)
					* cam.getViewPortH();
			y -= Assets.GFX_FLOWER1.getHeight(null) * scale;
			y += 5 * scale;
			float x = cam.getViewPortW();
			// x -= Assets.GFX_FLOWER1.getWidth(null) * scale / 2f;
			Doodad d = new Doodad(decoDoodad, this, x, y, stationarySpeed, 0,
					scale, scale);
			d.setVariation((int) (Math.random() * 4f + 1f));
			doodads.add(d);
		}

		if (Math.random() < 0.01) {
			float scale = 1f; // (float) Math.random() * 0.3f + 0.7f;
			float y = cam.getViewPortH()
					- map.getGroundSample(Constants.MAP_GROUNDSAMPLES - 1)
					* cam.getViewPortH();
			y -= Assets.GFX_CARROTS.getHeight(null) * scale;
			y += 30 * scale;
			Doodad d = new Doodad(DoodadType.CARROTS, this, cam.getViewPortW(),
					y, stationarySpeed, 0, scale, scale);
			d.setScoreValue(Constants.VALUE_CARROTS);
			d.setPickup(true);
			doodadsBg.add(d);
		}

		if (Math.random() < 0.05) {
			float scale = (float) Math.random();
			float y = cam.getViewPortH()
					- map.getGroundSample(Constants.MAP_GROUNDSAMPLES - 1)
					* cam.getViewPortH();
			y += Assets.GFX_ROCK1.getHeight(null) * scale;
			y += 5 * scale + 50;
			Doodad d = new Doodad(DoodadType.ROCK, this, cam.getViewPortW(), y,
					stationarySpeed, 0, scale, scale);
			d.setVariation((int) (Math.random() * 4f + 1f));
			doodads.add(d);
		}

		if (Math.random() < 0.1) {
			float scale = (float) Math.random();
			float y = cam.getViewPortH()
					- map.getGroundSample(Constants.MAP_GROUNDSAMPLES - 1)
					* cam.getViewPortH();
			y += Assets.GFX_DIRT1.getHeight(null) * scale;
			y += 5 * scale + 50;
			Doodad d = new Doodad(DoodadType.DIRT, this, cam.getViewPortW(), y,
					stationarySpeed, 0, scale, scale);
			d.setVariation((int) (Math.random() * 4f + 1f));
			doodads.add(d);
		}
	}

	public void init() {
		map = new Map(this);
		
		doodadsBg.clear();
		doodads.clear();
		baddies.clear();

		sun = new Doodad(DoodadType.SUN, this, cam.getViewPortW() - 50, 0,
				-10.3f, 0, 1, 1);
		doodadsBg.add(sun);

		level = -1;
		gameTime = 0;
		pl.setScore(0);
		pl.setCreeps(0);
		pl.setCarrots(0);
		pl.setDying(false);
		pl.walkLeft(false);
		pl.walkRight(false);
		pl.setLastLifeLostTime(this.gameTime);
		pl.setLives(3);

		nextMap();
	}

	public void nextMap() {
		doodads.clear();
		//baddies.clear();

		level++;
		int prepLevel = level % 6 + 1;

		if(level % 6 == 0) {
			map.genRands();
		}
		
		switch (prepLevel) {
		case 1:
			sun.setType(DoodadType.SUN);
			sun.setScale(1, 1);
			Renderer.setColors(ColorScheme.GRASS_DAY);
			decoDoodad = DoodadType.FLOWER;
			//Assets.changeMusic(Assets.MUS_1);
			break;
		case 2:
			sun.setType(DoodadType.MOON);
			sun.setScale(1, 1);
			Renderer.setColors(ColorScheme.GRASS_NIGHT);
			decoDoodad = DoodadType.FLOWER;
			//Assets.changeMusic(Assets.MUS_2);
			break;
		case 3:
			sun.setType(DoodadType.SUN);
			sun.setScale(0.7f, 0.7f);
			Renderer.setColors(ColorScheme.SNOW_DAY);
			decoDoodad = DoodadType.ICICLE;
			//Assets.changeMusic(Assets.MUS_3);
			break;
		case 4:
			sun.setType(DoodadType.MOON);
			sun.setScale(1.2f, 1.2f);
			Renderer.setColors(ColorScheme.SNOW_NIGHT);
			decoDoodad = DoodadType.ICICLE;
			//Assets.changeMusic(Assets.MUS_1);
			break;
		case 5:
			sun.setType(DoodadType.SUN);
			sun.setScale(1.5f, 1.5f);
			Renderer.setColors(ColorScheme.DESERT_DAY);
			decoDoodad = DoodadType.CACTUS;
			//Assets.changeMusic(Assets.MUS_2);
			break;
		case 6:
			sun.setType(DoodadType.MOON);
			sun.setScale(1.0f, 1.0f);
			Renderer.setColors(ColorScheme.DESERT_NIGHT);
			decoDoodad = DoodadType.CACTUS;
			//Assets.changeMusic(Assets.MUS_3);
			break;
		}

		sun.setX(cam.getViewPortW() - 50);
		sun.setVx(-0.3f);
		
		if(pl.getLives() < 4) {
			pl.setLives(pl.getLives() + 1);
		}
		
		lifeDoodads.clear();
		for(int i = 0; i < pl.getLives(); i ++) {
			Doodad lifedoo = new Doodad(DoodadType.LIFE, this, i * 50, 0, 0, 0, 1, 1);
			doodads.add(lifedoo);
			lifeDoodads.add(lifedoo);
		}
	}

	public Map getMap() {
		return this.map;
	}

	public ArrayList<Doodad> getDoodads() {
		return doodads;
	}

	public ArrayList<Doodad> getDoodadsBg() {
		return doodadsBg;
	}

	public ArrayList<Actor> getBaddies() {
		return baddies;
	}

	public double getGameTime() {
		return gameTime;
	}

	public Player getPlayer() {
		return this.pl;
	}

	public void startExpl(float x, float y) {
		float stationarySpeed = -Constants.GROUNDSPEED;

		float scale = 1f;
		Doodad d = new Doodad(DoodadType.EXPLOSION, this, x, y,
				stationarySpeed, 0, scale, scale);
		d.setVariation((int) (Math.random() * 4f + 1f));
		doodads.add(d);
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public GameState getState() {
		return state;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public void prepareMainMenu() {
		menu.getItems().clear();
		menu.addItem("newgame", "Press Enter to Play!");
		menu.setTopText("");
		menu.setBotText("Arrow keys to move, space to jump|Avoid onions, eat carrots, don't swim!|(Ignore the flying moles)");
	}

	public void handleMenu() {
		String ident = menu.getSel();

		if (ident == "newgame") {
			init();
			setState(GameState.GAME);
		}
		if (ident == "mainmenu") {
			prepareMainMenu();
			setState(GameState.MENU);
		}
		if (ident == "resume") {
			init();
			setState(GameState.GAME);
		}
	}
}
