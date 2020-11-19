import org.newdawn.slick.Color;

import jig.ConvexPolygon;
import jig.Vector;

public class Projectile extends PhysicsEntity {
	
	public Projectile(float x, float y, Vector v) {
		super(x, y, 0, 5f, 5f);
		setVelocity(v);
		setAcceleration(new Vector(0,0));
		this.addShape(new ConvexPolygon(10), Color.blue, Color.blue);
	}
	
	public void explode() {
		//
	}
	
	public void update(int delta, Terrain t) {
		super.update(delta, t);
		
		if(t.checkCollision((int)this.getX(), (int)this.getY(), (int)this.getCoarseGrainedRadius())) {
			System.out.println("collision: <" + (int)this.getX() + ", " + (int)this.getY() + ">");
		}
		
	}
	
}
