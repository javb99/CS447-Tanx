
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.geom.Circle;

import jig.ResourceManager;
import jig.Vector;


public class Terrain extends PhysicsEntity {

	private int height;
	private int width;
	private TerrainType mask[][];
	private Image currentImage;
	private ImageBuffer IB;
	private Image baseImage;
	
	enum TerrainType{
		OPEN,
		NORMAL
	}
	
	public Terrain(int w, int h, TerrainType t) {	//create a new terrain object completely of the specified type
		super(w/2, h/2, 0, new Vector(0,0));
		baseImage = ResourceManager.getImage(Tanx.TERRAIN_IMG);
		height = h;
		width = w;
		mask = new TerrainType[width][height];
		for(int x = 0; x < mask.length; x++) {
			for(int y = 0; y < mask[x].length; y++) {
				mask[x][y] = t;
			}
		}
		IB = new ImageBuffer(w, h);
		applyMask();
	}
	
	public Terrain(int w, int h, TerrainType m[][]) {	//make a new Terrain object from a given mask
		super(w/2,h/2, 0, new Vector(0,0));
		baseImage = ResourceManager.getImage(Tanx.TERRAIN_IMG);
		height = h;
		width = w;
		mask = m;
		IB = new ImageBuffer(w, h);
		applyMask();
	}
	
	private void applyMask(){
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				
				switch(mask[x][y]) {	//individually set every pixel in the image buffer according to the mask
				case OPEN:
					IB.setRGBA(x, y, 0, 0, 0, 0);
					break;
				case NORMAL:
					Color pixel = baseImage.getColor(x%baseImage.getWidth(), y%baseImage.getHeight());
					IB.setRGBA(x, y, pixel.getRed(), pixel.getGreen(), pixel.getBlue(), pixel.getAlpha());
				}
				
			}
		}
		
		this.removeImage(currentImage);	//remove old image
		currentImage = IB.getImage();	//get new image
		this.addImage(currentImage);	//apply new image
	}
	
	public boolean checkRectangularCollision(Vector p1, Vector p2) {	//check all bits in a rectangular area
		int x1 = (int)p1.getX();
		int y1 = (int)p1.getY();
		int x2 = (int)p2.getX();
		int y2 = (int)p2.getY();
		
		if(x1 < 0) x1 = 0;	//truncate if out of world
		if(x2 > width) x2 = width;
		if(y1 < 0) y1 = 0;
		if(y2 > height) y2 = height;
		
		for(int x = x1; x <= x2; x++) {
			for(int y = y1; y <= y2; y++) {
				if(mask[x][y] != TerrainType.OPEN) return true;
			}
		}
		return false;
	}
	
	public boolean checkCircularCollision(Vector p, float radius) {	//check all bits within radius of the given point
		int cx = (int)p.getX();
		int cy = (int)p.getY();
		
		int r = (int)radius;
		
		for(int x = cx - r; x <= cx + r; x++) {
			for(int y = cy - r; y <= cy + r; y++) {
				
				if(x < 0 || x >= width || y < 0 || y >= height) continue;	//dont check out of world
				if(Math.sqrt( Math.pow(Math.abs(x-cx), 2) + Math.pow(Math.abs(y-cy), 2) ) <= r) {	//pythagorean theorem
					
					if(mask[x][y] != TerrainType.OPEN) return true;
					
				}
			}
		}
		return false;
	}
	
	public boolean checkPointCollision(Vector p) {	//check a single pixel
		int x = (int)p.getX();
		int y = (int)p.getY();
		
		if(x < 0 || x >= width || y < 0 || y >= height) return false;	//dont check out of world
		
		if(mask[x][y] != TerrainType.OPEN) return true;
		return false;
	}
	
	public boolean checkLineCollision(Vector p1, Vector p2) {	//recursive function for lines
		int x1 = (int)p1.getX();
		int y1 = (int)p1.getY();
		int x2 = (int)p2.getX();
		int y2 = (int)p2.getY();
		
		
		if(mask[x1][y1] != TerrainType.OPEN) return true;	//if either endpoint is terrain, return true
		if(mask[x2][y2] != TerrainType.OPEN) return true;
		
		Vector p = new Vector((x1 + x2)/2, (y1 + y2)/2);	//find the midpoint
		
		if(p.epsilonEquals(p1, 0) || p.epsilonEquals(p2, 0)) return false;	//base case: our midpoint is one of the endpoints
		
		return(checkLineCollision(p1, p) || checkLineCollision(p, p2));	//check the two line halves;
	}
	
	public void setTerrainInRectangle(Vector p1, Vector p2, TerrainType t) {	//set all bits in a rectangular area
		int x1 = (int)p1.getX();
		int y1 = (int)p1.getY();
		int x2 = (int)p2.getX();
		int y2 = (int)p2.getY();
		
		if(x1 < 0) x1 = 0;	//truncate if out of world
		if(x2 >= width) x2 = width-1;
		if(y1 < 0) y1 = 0;
		if(y2 >= height) y2 = height-1;
		
		for(int x = x1; x <= x2; x++) {
			for(int y = y1; y <= y2; y++) {
				mask[x][y] = t;
			}
		}
		applyMask();
	}
	
	public void setTerrainAtPoint(Vector p, TerrainType t) {	//set a single bit
		int x = (int)p.getX();
		int y = (int)p.getY();
		
		if(x < 0 || x >= width || y < 0 || y >= height) return;	//dont set out of world
		
		mask[x][y] = t;
		applyMask();
	}
	
	public void setTerrainInCircle(Vector p, float radius, TerrainType t) {	//set all bits within radius at given point
		int cx = (int)p.getX();
		int cy = (int)p.getY();
		
		int r = (int)radius;
		
		if(r == 0) return;
		
		for(int x = cx - r; x <= cx + r; x++) {
			for(int y = cy - r; y <= cy + r; y++) {
				
				if(x < 0 || x >= width || y < 0 || y >= height) continue;	//dont set out of world
				
				if(Math.sqrt( Math.pow(Math.abs(x-cx), 2) + Math.pow(Math.abs(y-cy), 2) ) <= r) {	//pythagorean theorem
					mask[x][y] = t;
				}
			}
		}
		applyMask();
	}
	
	public void setTerrainInLine(Vector p1, Vector p2, TerrainType t) {	//first call for setting bits in a line
		int x1 = (int)p1.getX();
		int y1 = (int)p1.getY();
		int x2 = (int)p2.getX();
		int y2 = (int)p2.getY();
		
		
		mask[x1][y1] = t;	//set the endpoints
		mask[x2][y2] = t;
		
		Vector p = new Vector((x1 + x2)/2, (y1 + y2)/2);	//find the midpoint
		
		if(p.epsilonEquals(p1, 0) || p.epsilonEquals(p2, 0)) return;	//base case: midpoint is one of our endpoints
		
		setTerrainInLineAuxillary(p1, p, t);	//auxillary recursive functions, so we dont apply the mask at each step of recursion
		setTerrainInLineAuxillary(p, p2, t);
		
		applyMask();
	}
	
	private void setTerrainInLineAuxillary(Vector p1, Vector p2, TerrainType t) {	//auxillary for the above function, omitting mask application
		int x1 = (int)p1.getX();
		int y1 = (int)p1.getY();
		int x2 = (int)p2.getX();
		int y2 = (int)p2.getY();
		
		
		mask[x1][y1] = t;
		mask[x2][y2] = t;
		
		Vector p = new Vector((x1 + x2)/2, (y1 + y2)/2);
		
		if(p.epsilonEquals(p1, 0) || p.epsilonEquals(p2, 0)) return;
		
		setTerrainInLineAuxillary(p1, p, t);
		setTerrainInLineAuxillary(p, p2, t);
		
		
	}
	
	public void changeTerrainInCircle(Vector p, float radius, TerrainType targetType, TerrainType newType, boolean update) {
		int cx = (int)p.getX();
		int cy = (int)p.getY();
		
		int r = (int)radius;
		
		for(int x = cx - r; x <= cx + r; x++) {
			for(int y = cy - r; y <= cy + r; y++) {
				
				if(x < 0 || x >= width || y < 0 || y >= height) continue;	//dont set out of world
				
				if(Math.sqrt( Math.pow(Math.abs(x-cx), 2) + Math.pow(Math.abs(y-cy), 2) ) <= r) {	//pythagorean theorem
					if(mask[x][y] == targetType) {
						mask[x][y] = newType;
					}
				}
			}
		}
		if(update) {
			applyMask();
		}
	}
	
	public void changeTerrainInCircleList(ArrayList<Circle> list, TerrainType targetType, TerrainType newType) {
		for(int i = 0; i < list.size(); i++) {
			
			int cx = (int)list.get(i).getCenterX();
			int cy = (int)list.get(i).getCenterY();
			int r = (int)list.get(i).getRadius();
			
			for(int x = cx - r; x <= cx + r; x++) {
				for(int y = cy - r; y <= cy + r; y++) {
					
					if(x < 0 || x >= width || y < 0 || y >= height) continue;	//dont set out of world
					
					if(Math.sqrt( Math.pow(Math.abs(x-cx), 2) + Math.pow(Math.abs(y-cy), 2) ) <= r) {	//pythagorean theorem
						if(mask[x][y] == targetType) {
							mask[x][y] = newType;
						}
					}
				}
			}
		}
		applyMask();
	}
	
	/*private void printMask() {	//this function is used for debugging, NEVER call it in practice, it prints info about each individual pixel
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				System.out.println("x: " + x + " y: " + y + " m: " + mask[x][y]);
			}
		}
		
	}*/
}
