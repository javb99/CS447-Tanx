import org.newdawn.slick.Color;

import jig.ConvexPolygon;
import jig.Entity;

class TerrainTile extends PhysicsEntity {
	public TerrainTile(Color c) {
	  super(0, 0, 0, 0, 0);
		super.addShape(new ConvexPolygon((float)World.tileLength, (float)World.tileLength), Color.transparent, c);
	}
}