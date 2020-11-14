import org.newdawn.slick.Color;

import jig.ConvexPolygon;
import jig.Entity;

class TerrainTile extends Entity {
	public TerrainTile(Color c) {
		super.addShape(new ConvexPolygon((float)World.tileLength, (float)World.tileLength), Color.transparent, c);
	}
}