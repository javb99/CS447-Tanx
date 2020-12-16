import org.newdawn.slick.Color;

import jig.ConvexPolygon;
import jig.Vector;

public class IceBomb extends Projectile {
	
	public IceBomb(float x, float y, Vector v, int radius, int damage) {
		super(x, y, v, radius, damage);
		this.addShape(new ConvexPolygon(10), Color.cyan, Color.cyan);
		this.TI = TerrainInteraction.ICE_BOMB;
	}

}
