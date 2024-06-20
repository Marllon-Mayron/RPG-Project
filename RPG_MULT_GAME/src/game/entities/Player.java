package game.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.PrintStream;

import game.main.Game;

public class Player extends Entity{
	public int id = 0;
	public String name = "";
	public PrintStream ps;
	public boolean shoot;
	public Player(int id, int x, int y, int width, int height, BufferedImage sprite, PrintStream ps) {
		super(x, y, width, height, sprite);
		
		this.id = id;
		this.ps = ps;
		this.mwidth = 6;
		this.mheight = 8;
	}
	
	public void tick() {		
		positionUpdates();

		CheckInterations();
	}
	
	//METODO USADO PRA ATUALIZAR AS POSICOES DOS JOGADORES
	public void positionUpdates(){
		if(right && x < Game.WIDTH-6) {
			x++;
		}if(left && x > 0) {
			x--;
		}if(up && y + 8 > 0) {
			y--;
		}if(down && y < Game.HEIGHT-6) {
			y++;
		}
		try{
			ps.println("walk,"+(int)x+","+(int)y);
		}catch(Exception e) {
			
		}
	}
	public void CheckInterations() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Player && (!e.equals(this))) {
				if(Entity.isCollidding(e, this)) {
					//System.out.println("COLISÃO");
				}
				
			}
		}
		
	}
	public void disconnect() {
		ps.println("disconnect");
	}
	public void render(Graphics g) {
		g.setColor(Color.red);
		
		g.fillRect((int)x,(int)y, width, height);
		
		g.setColor(Color.blue);
		g.fillRect((int)x,(int)y+9, mwidth, mheight);
	}
	public void renderText(Graphics g) {
		g.setColor(Color.white);
		
		g.drawString(name +" ("+id+")",((int)x -4) * Game.SCALE,((int)y -4)* Game.SCALE);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
