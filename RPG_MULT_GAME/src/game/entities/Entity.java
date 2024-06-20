package game.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;




public class Entity {
	
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	
	public int maskx;
	public int masky;
	public int mwidth;
	public int mheight;
	
	protected BufferedImage sprite;
	
	protected double speed;
	public boolean right, left, up, down;


	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
	}

	

	public double getX() {
		return x;
	}



	public void setX(double x) {
		this.x = x;
	}



	public double getY() {
		return y;
	}



	public void setY(double y) {
		this.y = y;
	}

	public void setMask(int maskx, int masky, int mwidth, int mheight) {
		this.maskx = maskx;
		this.masky = masky;
		this.mwidth = mwidth;
		this.mheight = mheight;
	}
	public static boolean isCollidding(Entity e1, Entity e2) {
		Rectangle e1Mask = new Rectangle();
		Rectangle e2Mask = new Rectangle();
		
		e1Mask.x = (int)e1.x;
		e1Mask.y = (int)e1.y;
		e1Mask.width = e1.width;
		e1Mask.height = e1.height;
		
		e2Mask.x = (int)e2.x;
		e2Mask.y = (int)e2.y;
		e2Mask.width = e2.width;
		e2Mask.height = e2.height;
		
		return e1Mask.intersects(e2Mask);
	}
	public static void isColliddingWithObjects(Entity en) {
		 if(en.right == true) {
			 en.x -= en.speed; 
		 }else if(en.left == true) {
			 en.x += en.speed;
		 }else if(en.up == true) {
			 en.y += en.speed;
		 }else if(en.down == true) {
			 en.y -= en.speed;
		 }
		 
	}


	public void tick() {

	}

	public void render(Graphics g) {
		g.drawImage(sprite, (int)this.getX(), (int)this.getY(), null);
	}
	public void renderText(Graphics g) {
		
	}
}
