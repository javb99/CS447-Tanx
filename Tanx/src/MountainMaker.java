import org.newdawn.slick.Color;

import jig.ConvexPolygon;
import jig.Vector;

public class MountainMaker extends Projectile {

	public MountainMaker(float x, float y, Vector v, int radius, int damage) {
		super(x, y, v, radius, damage);
		this.addShape(new ConvexPolygon(10), Color.darkGray, Color.darkGray);
		this.TI = TerrainInteraction.MOUNTAIN_MAKER;
	}
	
}
