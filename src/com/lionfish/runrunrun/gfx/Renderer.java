package com.lionfish.runrunrun.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import com.lionfish.runrunrun.gamelogic.Camera;
import com.lionfish.runrunrun.gamelogic.Constants;
import com.lionfish.runrunrun.gamelogic.Game;
import com.lionfish.runrunrun.main.Assets;
import com.lionfish.runrunrun.main.Menu;
import com.lionfish.runrunrun.main.Menu.MenuItem;
import com.lionfish.runrunrun.map.Actor;
import com.lionfish.runrunrun.map.Doodad;
import com.lionfish.runrunrun.map.Map;
import com.lionfish.runrunrun.map.Player;

public class Renderer {
	public enum ColorScheme {
		GRASS_DAY(new Color(0, 250, 0), new Color(153, 69, 6), Color.BLUE,
				new Color(100, 100, 200)), GRASS_NIGHT(new Color(0, 91, 24),
				new Color(107, 46, 0), Color.BLACK, new Color(0, 0, 100)), SNOW_DAY(
				new Color(200, 200, 200), new Color(100, 100, 150), Color.BLUE,
				new Color(200, 200, 250)), SNOW_NIGHT(new Color(100, 100, 100),
				new Color(20, 20, 60), Color.BLACK, new Color(100, 100, 150)), DESERT_DAY(
				new Color(250, 190, 0), new Color(255, 237, 167), new Color(7,
						255, 222), new Color(68, 217, 255)), DESERT_NIGHT(
				new Color(170, 130, 0), new Color(198, 184, 129), new Color(2,
						86, 75), new Color(47, 150, 175));
		public Color bottom;
		public Color top;
		public Color sky;
		public Color water;

		private ColorScheme(Color top, Color bottom, Color sky, Color water) {
			this.bottom = bottom;
			this.top = top;
			this.sky = sky;
			this.water = water;
		}
	};

	private static boolean flashingScore = false;
	private static float flashingScoreT = 0;
	private static ColorScheme colors;
	private static long flashingPlayerStart;
	private static float flashingPlayerT = 0;
	private static boolean flashingPlayer = false;

	// new fields
	private static int[] xPoints = new int[Constants.MAP_GROUNDSAMPLES + 2];
	private static int[] yPoints = new int[Constants.MAP_GROUNDSAMPLES + 2];
	private static int[] yPointsE = new int[Constants.MAP_GROUNDSAMPLES + 2];

	public static void renderGround(Graphics2D g, Map map, Camera cam) {

		int samples = Constants.MAP_GROUNDSAMPLES;

		int nPoints = samples + 2;
		// int[] xPoints = new int[nPoints];
		// int[] yPoints = new int[nPoints];
		// int[] yPointsE = new int[nPoints];
		int c = 0;

		for (int i = 0; i < samples; i++) {
			int px = (int) (i * (float) cam.getViewPortW() / (float) (Constants.MAP_GROUNDSAMPLES - 1));
			int py = (int) ((1f - map.getGroundSample(i)) * cam.getViewPortH());

			xPoints[c] = (int) (px - map.getScrollDelta());
			yPoints[c] = py;
			yPointsE[c] = py + 40;
			c++;
		}

		xPoints[c - 1] = cam.getViewPortW();
		xPoints[c + 0] = cam.getViewPortW();
		yPoints[c + 0] = yPointsE[c + 0] = cam.getViewPortH();
		xPoints[c + 1] = 0;
		yPoints[c + 1] = yPointsE[c + 1] = cam.getViewPortH();

		g.setColor(colors.top);
		g.fillPolygon(xPoints, yPoints, nPoints);

		g.setColor(colors.bottom);
		g.fillPolygon(xPoints, yPointsE, nPoints);

	}

	public static void renderDoodads(Graphics2D g2, ArrayList<Doodad> doodads) {
		// TODO: better way of solving the concurrent modification shit show?
		// ArrayList<Doodad> copy = new ArrayList<Doodad>(doodads);
		// for(Doodad d : copy) {
		for (int i = 0; i < doodads.size(); i++) {
			Doodad d = doodads.get(i);
			Image img = d.getGfx();
			if (img == null)
				continue;
			g2.drawImage(img, (int) d.getX(), (int) d.getY(),
					(int) (img.getWidth(null) * d.getScaleX()),
					(int) (img.getHeight(null) * d.getScaleY()), null);
		}
	}

	public static void renderSky(Graphics2D g2, Camera cam) {
		g2.setBackground(colors.sky);
		g2.clearRect(0, 0, cam.getViewPortW(), cam.getViewPortH());

		g2.setColor(colors.water);
		g2.fillRect(0,
				(int) (cam.getViewPortH() * (1f - Constants.WATER_HEIGHT)),
				cam.getViewPortW(),
				(int) (cam.getViewPortH() * Constants.WATER_HEIGHT));

		// g2.drawImage(Assets.GFX_SUN, (int) (cam.getViewPortW() * 0.7), 50,
		// null);
	}

	public static void renderPlayer(Graphics2D g2, Player pl) {
		// g2.scale(0.4, 0.4);
		if (flashingPlayer) {
			if (System.currentTimeMillis() - flashingPlayerStart > 3000) {
				flashingPlayer = false;
			} else {
//				float t = System.currentTimeMillis() - flashingPlayerStart;
				flashingPlayerT += 0.5f;
				if (Math.cos(flashingPlayerT) > 0) {
					return;
				}
			}
		}

		Image img = Assets.anim(pl.getAnim(), pl);

		float w = img.getWidth(null) * pl.getScaleX();
		float h = img.getHeight(null) * pl.getScaleY();

		AffineTransform at = new AffineTransform();

		at.translate(pl.getX(), pl.getY());
		at.rotate(pl.getR(), w / 2f, h / 2f);
		at.scale(pl.getScaleX(), pl.getScaleY());

		g2.drawImage(img, at, null);
	}

	public static void renderBaddies(Graphics2D g2, ArrayList<Actor> baddies) {
		// TODO: better way of solving the concurrent modification shit show?
		// ArrayList<Actor> copy = new ArrayList<Actor>(baddies);
		// for(Actor a : copy) {
		for (int i = 0; i < baddies.size(); i++) {
			Actor a = baddies.get(i);
			Image img = Assets.anim(a.getAnim(), a);

			float w = img.getWidth(null) * a.getScaleX();
			float h = img.getHeight(null) * a.getScaleY();

			AffineTransform at = new AffineTransform();

			at.translate(a.getX(), a.getY());
			at.rotate(a.getR(), w / 2f, h / 2f);
			at.scale(a.getScaleX(), a.getScaleY());

			if (a.isFlipped()) {
				at.translate(w, 0);
				at.scale(-1, 1);
			}

			g2.drawImage(img, at, null);
			
		}
	}

	public static void renderHUD(Graphics2D g, Game game) {
		Font oldFont = g.getFont();

		String scoreText = String.format("%.0f", game.getPlayer().getScore());
		float scoreFontSize = 50;
		if (flashingScore) {
			scoreFontSize += Math.abs(Math.cos(flashingScoreT)) * 50;
			flashingScoreT += 0.2f;

			if (flashingScoreT > 10) {
				flashingScore = false;
			}
		}

		Font scoreFont = new Font("sansserif", Font.BOLD, (int) scoreFontSize);

		g.setColor(Color.red);
		g.setFont(scoreFont);
		
		g.drawString(scoreText,
				(float) (game.getCam().getViewPortW() / 2 - g.getFontMetrics()
						.getStringBounds(scoreText, g).getWidth() / 2),
				15 + scoreFontSize / 2);

		// g.setFont(new Font("sansserif", Font.BOLD, 70));
		// g.setColor(Color.white);
		// g.drawString("" + game.getPlayer().getLives(), 0, 50);

		g.setFont(oldFont);
	}

	public static void flashScore() {
		if (!flashingScore)
			flashingScoreT = 0;
		flashingScore = true;
	}

	public static void setColors(ColorScheme scheme) {
		colors = scheme;
	}

	public static void renderMenu(Graphics2D g, Game game) {
		Menu menu = game.getMenu();
		Font oldFont = g.getFont();

		float fontSize = 70;

		float selFontSize = (float) (fontSize + Math.abs(Math
				.cos(flashingScoreT * 2)) * 10);
		flashingScoreT += 0.05f;

		Font font = new Font("sansserif", Font.BOLD, (int) fontSize);
		Font selfont = new Font("sansserif", Font.BOLD, (int) selFontSize);
		Font topfont = new Font("sanserif", Font.BOLD, 40);

		g.setColor(Color.red);

		int y = (int) (game.getCam().getViewPortH() / 2 - (menu.getItems()
				.size() * fontSize * 1.2f) / 2);
		for (MenuItem item : menu.getItems()) {
			String text = item.text;
			if (item.isSelected) {
				g.setFont(selfont);
			} else {
				g.setFont(font);
			}
			g.drawString(text, (float) (game.getCam().getViewPortW() / 2 - g
					.getFontMetrics().getStringBounds(text, g).getWidth() / 2),
					y);

			y += fontSize * 1.2f;
		}

		String text = menu.getTopText();
		g.setColor(Color.white);
		g.setFont(topfont);
		g.drawString(text, (float) (game.getCam().getViewPortW() / 2 - g
				.getFontMetrics().getStringBounds(text, g).getWidth() / 2), 100);

		if (menu.getBotText().length() > 0) {
			String[] parts = menu.getBotText().split("\\|");

			//Font botfont = new Font("sansserif", Font.PLAIN, 20);
			g.setColor(Color.white);
			g.setFont(topfont);

			text = parts[0];
			g.drawString(text, (float) (game.getCam().getViewPortW() / 2 - g
					.getFontMetrics().getStringBounds(text, g).getWidth() / 2),
					game.getCam().getViewPortH() - 120);
			text = parts[1];
			g.drawString(text, (float) (game.getCam().getViewPortW() / 2 - g
					.getFontMetrics().getStringBounds(text, g).getWidth() / 2),
					game.getCam().getViewPortH() - 70);
			text = parts[2];
			g.drawString(text, (float) (game.getCam().getViewPortW() / 2 - g
					.getFontMetrics().getStringBounds(text, g).getWidth() / 2),
					game.getCam().getViewPortH() - 20);
		}

		g.setFont(oldFont);
	}

	public static void flashPlayer() {
		flashingPlayer = true;
		flashingPlayerT = 0;
		flashingPlayerStart = System.currentTimeMillis();
	}
}
