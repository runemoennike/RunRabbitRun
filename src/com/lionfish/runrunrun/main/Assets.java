package com.lionfish.runrunrun.main;


import java.applet.Applet;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Assets {

	public static Image GFX_CLOUD;
	public static Image GFX_FLOWER1;
	public static Image GFX_FLOWER2;
	public static Image GFX_FLOWER3;
	public static Image GFX_ICICLE1;
	public static Image GFX_ICICLE2;
	public static Image GFX_ICICLE3;
	public static Image GFX_CACTUS1;
	public static Image GFX_CACTUS2;
	public static Image GFX_CACTUS3;
	public static Image GFX_ROCK1;
	public static Image GFX_ROCK2;
	public static Image GFX_ROCK3;
	public static Image GFX_DIRT1;
	public static Image GFX_DIRT2;
	public static Image GFX_DIRT3;
	public static Image GFX_SUN;
	public static Image GFX_MOON;
	public static Image GFX_CARROTS;
	public static Image GFX_LIFE;
	public static Image GFX_X2;
	public static Image GFX_X3;
	public static Image GFX_X4;

//	public static Sequence MUS_1;
//	public static Sequence MUS_2;
//	public static Sequence MUS_3;
//	
//	private static Sequencer seq = null;
	
	private static boolean loaded = false;
	
	
	public static HashMap<String, Anim> anims = new HashMap<String, Assets.Anim>();
	public static HashMap<Object, AnimCntl> animc = new HashMap<Object, Assets.AnimCntl>();
	
	public static void load(Applet ap) {
		
		System.out.println(ap.getCodeBase());
		GFX_CLOUD = loadAcc("/res/cloud.png");
		GFX_FLOWER1 = loadAcc("/res/flower1.png");
		GFX_FLOWER2 = loadAcc("/res/flower2.png");
		GFX_FLOWER3 = loadAcc("/res/flower3.png");
		GFX_ICICLE1 = loadAcc("/res/icicle1.png");
		GFX_ICICLE2 = loadAcc("/res/icicle2.png");
		GFX_ICICLE3 = loadAcc("/res/icicle3.png");
		GFX_CACTUS1 = loadAcc("/res/cactus1.png");
		GFX_CACTUS2 = loadAcc("/res/cactus2.png");
		GFX_CACTUS3 = loadAcc("/res/cactus3.png");
		GFX_ROCK1 = loadAcc("/res/rock1.png");
		GFX_ROCK2 = loadAcc("/res/rock2.png");
		GFX_ROCK3 = loadAcc("/res/rock3.png");
		GFX_DIRT1 = loadAcc("/res/dirt1.png");
		GFX_DIRT2 = loadAcc("/res/dirt2.png");
		GFX_DIRT3 = loadAcc("/res/dirt3.png");
		GFX_SUN = loadAcc("/res/sun.png");
		GFX_MOON = loadAcc("/res/moon.png");
		GFX_CARROTS = loadAcc("/res/carrots.png");
		GFX_LIFE = loadAcc("/res/life.png");
		GFX_X2 = loadAcc("/res/x2.png");
		GFX_X3 = loadAcc("/res/x3.png");
		GFX_X4 = loadAcc("/res/x4.png");

//		try {
//			seq = MidiSystem.getSequencer();
//			seq.open();
//			
//			Synthesizer synthesizer = MidiSystem.getSynthesizer();
//			Receiver receiver = synthesizer.getReceiver();
//			Transmitter transmitter = seq.getTransmitter();
//			transmitter.setReceiver(receiver);
//			
//			MUS_1 = MidiSystem.getSequence(new File("res/COUNTRY.MID"));
//			MUS_2 = MidiSystem.getSequence(new File("res/ROCK.MID"));
//			MUS_3 = MidiSystem.getSequence(new File("res/TECHNO.MID"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		loadAnims(ap);
		
		loaded = true;
	}
	
	public static boolean isLoaded() {
		return loaded;
	}

	public static Image loadAcc(String file) {
		Image sourceImage = null;
		try {
			sourceImage = ImageIO.read(Assets.class.getResource(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gc.createCompatibleImage(sourceImage.getWidth(null),sourceImage.getHeight(null),Transparency.TRANSLUCENT);
		image.getGraphics().drawImage(sourceImage,0,0,null);
		
		System.out.println("Assets: " + file + "(" + image.getWidth(null) + "x" + image.getHeight(null) + ")");
		
		return image;
	}
	
	public static void loadAnims(Applet ap) {
		try {
			InputStream is = Assets.class.getResourceAsStream("/res/anims.lst");
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			String line;
			
			while((line = in.readLine()) != null) {
				String[] parts = line.split(",");
				String base = parts[0];
				int count = Integer.parseInt(parts[1]);
				int rate = Integer.parseInt(parts[2]);
				boolean loop = Boolean.parseBoolean(parts[3]);
				
				Anim an = new Anim();
				an.imgs = new Image[count];
				an.rate = rate;
				an.loop = loop;
				
				for(int i = 1; i <= count; i ++) {
					an.imgs[i - 1] = loadAcc("/res/" + base + "_" + i + ".png");
				}
				
				anims.put(base, an);
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Image anim(String base, Object owner) {
		AnimCntl cntl = new AnimCntl(base);
		if(animc.containsKey(owner)) {
			cntl = animc.get(owner);
			if(!cntl.anim.equals(base)) {
				cntl = new AnimCntl(base);
				animc.put(owner, cntl);		
			}
		} else {
			animc.put(owner, cntl);
		}
		
		Anim an = anims.get(base);
		if(an == null) System.out.println(base);
		Image img = an.imgs[cntl.frame];
		
		if(System.currentTimeMillis() - cntl.stamp > an.rate) {
			cntl.stamp = System.currentTimeMillis();
			cntl.frame ++;
			if(cntl.frame >= an.imgs.length) {
				if(an.loop) { 
					cntl.frame = 0;
				} else {
					cntl.frame = an.imgs.length - 1;
				}
				
			}
		}
		
		return img;
	}
	
	private static class Anim {
		public Image[] imgs;
		public int rate;
		public boolean loop;
	}
	
	private static class AnimCntl {
		public int frame = 0;
		public long stamp = 0;
		public String anim;
		
		public AnimCntl(String anim) {
			this.anim = anim;
		}
	}

//	public static void changeMusic(Sequence mus) {
//		try {
//			seq.setSequence(mus);
//			seq.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
//			seq.start();
//			System.out.println("Started " + mus.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("ignored.");
//		}
//	}
}
