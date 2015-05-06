package com.lionfish.runrunrun.gamelogic;

public class Camera {
	
	private final static float DAMPENING = 0.1f;
	private final static float SCALE_MIN = 0.5f;
	private final static float SCALE_MAX = 2f;

	private float x;
	private float y;
	private float zoom;
	private float velX;
	private float velY;
	private int viewPortW;
	private int viewPortH;

	public Camera() {
		this.zoom = 1f;
	}
	
	public void update(float t) {
		if(t < Constants.MINIMUM_TIME) t = Constants.MINIMUM_TIME;
		
		if (velX != 0) {
			x += velX * t;
			velX *= (1f - DAMPENING);
			if(Math.abs(velX) < 0.01f) {
				velX = 0;
			}
		}
		
		if (velY != 0) {
			y += velY * t;
			velY *= (1f - DAMPENING);
			if(Math.abs(velY) < 0.01f) {
				velY = 0;
			}
		}
		
	}

	public void move(float dx, float dy) {
		this.x += dx;
		this.y += dy;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZoom() {
		return zoom;
	}

	public void scale(float f) {
		this.zoom *= f;
		
		if(this.zoom < SCALE_MIN) this.zoom = SCALE_MIN;
		if(this.zoom > SCALE_MAX) this.zoom = SCALE_MAX;
	}

	public void accelerate(float x, float y) {
		this.velX = x;
		this.velY = y;
	}

	public void halt() {
		this.velX = 0;
		this.velY = 0;
	}

	public void setViewPortDims(int width, int height) {
		this.viewPortW = width;
		this.viewPortH = height;
	}
	public int getViewPortW() {
		return viewPortW;
	}

	public int getViewPortH() {
		return viewPortH;
	}
	
}
