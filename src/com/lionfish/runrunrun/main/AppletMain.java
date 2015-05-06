package com.lionfish.runrunrun.main;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.lionfish.runrunrun.gamelogic.Camera;
import com.lionfish.runrunrun.gamelogic.Game;
import com.lionfish.runrunrun.gfx.Renderer;

public class AppletMain extends java.applet.Applet implements Runnable,
		KeyListener {

	private static final long serialVersionUID = 1L;

	private Image bgImg;
	private Image worldMapImg;

	private Image buffer;
	private Dimension dim;

	private Game game;
	private double fpsLogic, fpsLogicA, fpsGfx;
	private long fpsGfxT, fpsGfxC;

	private String typeBuffer = "";

	public AppletMain() {

		game = new Game();

		game.prepareMainMenu();
	}

	@Override
	public void init() {
		super.init();

		this.setSize(750, 600);

		dim = getSize();
		buffer = createImage(dim.width, dim.height);


		this.game.getCam().setViewPortDims(this.getWidth(), this.getHeight());
		game.init();
		
		if(this.getParameter("fbid") != null) {
			game.getPlayer().setFbId(Integer.parseInt(this.getParameter("fbid")));
			game.getPlayer().setFbName(this.getParameter("fbname"));
			game.getPlayer().setFbLink(this.getParameter("fblink"));
		}

		this.addKeyListener(this);
	}

	@Override
	public void start() {
		super.start();
		new Thread(this).start();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if(!Assets.isLoaded()) {
			g.drawString("Loading...", 0, 10);
			return;
		}
		
		Camera cam = this.game.getCam();

		Graphics2D g2 = (Graphics2D) g;

		Renderer.renderSky(g2, cam);
		Renderer.renderDoodads(g2, this.game.getDoodadsBg());
		Renderer.renderGround(g2, this.game.getMap(), cam);
		Renderer.renderBaddies(g2, this.game.getBaddies());
		Renderer.renderDoodads(g2, this.game.getDoodads());

		switch (game.getState()) {
		case GAME:
			Renderer.renderHUD(g2, this.game);
			Renderer.renderPlayer(g2, this.game.getPlayer());
			break;
		case MENU:
			Renderer.renderMenu(g2, this.game);
			break;
		}

		// Stat dump
		/*g2.setColor(Color.white);
		g2.drawString("FPS: " + (int) fpsLogic + " / " + (int) fpsLogicA
				+ " / " + (int) fpsGfx, 0, 510);

		int numDoodads = game.getDoodads().size() + game.getDoodadsBg().size();
		int numActors = game.getBaddies().size() + 1;
		g2.drawString(numDoodads + " doodads / " + numActors + " actors", 0,
				520);

		g2.drawString(
				"Score: " + String.format("%.2f", game.getPlayer().getScore())
						+ " Lives: " + game.getPlayer().getLives(), 0, 530);
		
		g2.drawString(
				"fbid: " + game.getPlayer().getFbId() + " fbname: " + game.getPlayer().getFbName(), 0, 540);

		*/
		
		// FPS
		fpsGfxC++;
		if (System.currentTimeMillis() - fpsGfxT >= 1000) {
			fpsGfxT = System.currentTimeMillis();
			fpsGfx = fpsGfxC;
			fpsGfxC = 0;
		}
	}

	@Override
	public void update(Graphics g) {
		paint(buffer.getGraphics());
		g.drawImage(buffer, 0, 0, null);
	}

	@Override
	public void run() {
		Assets.load(this);
		
		long t = System.currentTimeMillis();
		long fpsT = t;
		int fpsC = 0;

		
		this.requestFocus();
		

		while (true) {
			float dt = (System.currentTimeMillis() - t) / 1000f;
			t = System.currentTimeMillis();

			game.update(dt);
			repaint();

			fpsC++;
			fpsLogic = 1d / dt;
			if (t - fpsT > 1000) {
				fpsLogicA = fpsC;
				fpsC = 0;
				fpsT = t;
			}
			// System.out.println("fps: " + fps);

			try {
				Thread.sleep(20);
			} catch (InterruptedException ex) {
				break;
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (this.game.getState()) {
		case GAME:
			switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				game.getPlayer().walkRight(true);
				break;
			case KeyEvent.VK_LEFT:
				game.getPlayer().walkLeft(true);
				break;
			case KeyEvent.VK_SPACE:
				game.getPlayer().jump(true);
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (this.game.getState()) {
		case GAME:
			switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				game.getPlayer().walkRight(false);
				break;
			case KeyEvent.VK_LEFT:
				game.getPlayer().walkLeft(false);
				break;
			case KeyEvent.VK_SPACE:
				game.getPlayer().jump(false);
				break;
			case KeyEvent.VK_ESCAPE:
				// handlePause();
				break;
			}
			break;
		case MENU:
			switch (e.getKeyCode()) {
			case KeyEvent.VK_DOWN:
				game.getMenu().nav(1);
				break;
			case KeyEvent.VK_UP:
				game.getMenu().nav(-1);
				break;
			//case KeyEvent.VK_SPACE:
			case KeyEvent.VK_ENTER:
				game.handleMenu();
				break;
			}
			break;
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		typeBuffer += e.getKeyChar();
		if (typeBuffer.length() > 32) {
			typeBuffer = typeBuffer.substring(1);
		}
		if (typeBuffer.endsWith("pjaskebamse")) {
			game.getPlayer().setLastLifeLostTime(Double.MAX_VALUE);
			game.getPlayer().setFbId(0);
			game.getPlayer().setFbName("");
			typeBuffer = "";
		}
		if (typeBuffer.endsWith("fam")) {
			game.nextMap();
			game.getPlayer().setFbId(0);
			game.getPlayer().setFbName("");
			typeBuffer = "";
		}
	}

}
