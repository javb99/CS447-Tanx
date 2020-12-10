import org.newdawn.slick.Color;

import jig.ConvexPolygon;
import jig.Vector;

public class MountainMaker extends Projectile {

	public MountainMaker(float x, float y, Vector v) {
		super(x, y, v);
		this.addShape(new ConvexPolygon(10), Color.darkGray, Color.darkGray);
		this.type = Type.MOUNTAIN_MAKER;
	}
	
}
