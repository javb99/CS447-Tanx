import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;

import jig.Entity;


public class Terrain extends Entity {

	private int height;
	private int width;
	private int mask[][];
	private Image currentImage;
	private ImageBuffer IB;
	
	public Terrain(int w, int h, int m[][]) {
		super(w/2,h/2);
		height = h;
		width = w;
		mask = m;
		IB = new ImageBuffer(w, h);
		applyMask();
	}
	
	private void applyMask(){
		int x;
		int y;
		for(x = 0; x < width; x++) {
			for(y = 0; y < height; y++) {
				IB.setRGBA(x, y, 135, 88, 43, mask[x][y] * 255);
			}
		}
		
		this.removeImage(currentImage);
		currentImage = IB.getImage();
		this.addImage(currentImage);
	}
	
	public boolean checkCollision(int x1, int y1, int x2, int y2) {
		if(x1 < 0) x1 = 0;
		if(x2 > width) x2 = width;
		if(y1 < 0) y1 = 0;
		if(y2 > height) y2 = height;
		
		for(int x = x1; x <= x2; x++) {
			for(int y = y1; y <= y2; y++) {
				if(mask[x][y] == 1) return true;
			}
		}
		return false;
	}
	
	public boolean checkCollision(int cx, int cy, int r) {
		for(int x = cx - r; x <= cx + r; x++) {
			for(int y = cy - r; y <= cy + r; y++) {
				
				if(x < 0 || x >= width || y < 0 || y >= height) continue;
				if(Math.sqrt( Math.pow(Math.abs(x-cx), 2) + Math.pow(Math.abs(y-cy), 2) ) <= r) {	//pythagorean theorem
					
					if(mask[x][y] == 1) return true;
					
				}
			}
		}
		return false;
	}
	
	public boolean checkCollision(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height) return false;
		
		if(mask[x][y] == 1) return true;
		return false;
	}
	
	public void removeTerrain(int x1, int y1, int x2, int y2) {
		if(x1 < 0) x1 = 0;
		if(x2 >= width) x2 = width-1;
		if(y1 < 0) y1 = 0;
		if(y2 >= height) y2 = height-1;
		
		for(int x = x1; x <= x2; x++) {
			for(int y = y1; y <= y2; y++) {
				mask[x][y] = 0;
			}
		}
		applyMask();
	}
	
	public void removeTerrain(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height) return;
		
		mask[x][y] = 0;
		applyMask();
	}
	
	public void removeTerrain(int cx, int cy, int r) {
		for(int x = cx - r; x <= cx + r; x++) {
			for(int y = cy - r; y <= cy + r; y++) {
				
				if(x < 0 || x >= width || y < 0 || y >= height) continue;
				
				if(Math.sqrt( Math.pow(Math.abs(x-cx), 2) + Math.pow(Math.abs(y-cy), 2) ) <= r) {	//pythagorean theorem
					mask[x][y] = 0;
				}
			}
		}
		applyMask();
	}
	
	public void addTerrain(int x1, int y1, int x2, int y2) {
		if(x1 < 0) x1 = 0;
		if(x2 >= width) x2 = width-1;
		if(y1 < 0) y1 = 0;
		if(y2 >= height) y2 = height-1;
		
		for(int x = x1; x <= x2; x++) {
			for(int y = y1; y <= y2; y++) {
				mask[x][y] = 1;
			}
		}
		applyMask();
	}
	
	public void addTerrain(int cx, int cy, int r) {
		for(int x = cx - r; x <= cx + r; x++) {
			for(int y = cy - r; y <= cy + r; y++) {
				
				if(x < 0 || x >= width || y < 0 || y >= height) continue;
				
				if(Math.sqrt( Math.pow(Math.abs(x-cx), 2) + Math.pow(Math.abs(y-cy), 2) ) <= r) {	//pythagorean theorem
					mask[x][y] = 1;
				}
			}
		}
		applyMask();
	}
	
	public void addTerrain(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height) return;
		
		mask[x][y] = 1;
		applyMask();
	}
	
	public void printMask() {	//this function is used for debugging, NEVER call it in practice, it prints info about each individual pixel
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				System.out.println("x: " + x + " y: " + y + " m: " + mask[x][y]);
			}
		}
		
	}
}
