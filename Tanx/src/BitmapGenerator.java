import java.util.Date;
import java.util.Random;

public class BitmapGenerator {

	private int width;
	private int height;
	
	public BitmapGenerator(int w, int h) {
		width = w;
		height = h;
	}
	
	public void setWidth(int w) {width = w;}
	public void setHeight(int h) {height = h;}
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	
	public Terrain.TerrainType[][] generateRandomSineMap(){
		Terrain.TerrainType bitmap[][] = new Terrain.TerrainType[width][height];
		
		Date date = new Date();
		
		Random rand = new Random(date.getTime());
		
		double a = rand.nextDouble()*100;
		double b = rand.nextDouble()*100;
		double c = rand.nextDouble()*100;
		double d = rand.nextDouble()*100;
		
		double e = rand.nextDouble()*900+100;
		double f = rand.nextDouble()*900+100;
		double g = rand.nextDouble()*900+100;
		double h = rand.nextDouble()*900+100;
		/*
		System.out.println("a = " + a);
		System.out.println("b = " + b);
		System.out.println("c = " + c);
		System.out.println("d = " + d);
		System.out.println("e = " + e);
		System.out.println("f = " + f);
		System.out.println("g = " + g);
		System.out.println("h = " + h);
		*/
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				if(j < (a*Math.sin(i/e) + b*Math.cos(i/f) - c*Math.sin(i/g) - d*Math.cos(i/h) + (height - 450))) {
					bitmap[i][j] = Terrain.TerrainType.OPEN;
				} else {
					bitmap[i][j] = Terrain.TerrainType.NORMAL;
				}
			}
		}
		
		return bitmap;
	}
	
}
