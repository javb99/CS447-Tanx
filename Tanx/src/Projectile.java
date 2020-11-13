import org.newdawn.slick.Color;

import jig.ConvexPolygon;
import jig.Vector;

public class Projectile extends PhysicEntity {
	
	Vector velocity;
	
	
	public Projectile(float x, float y, Vector v) {
		super(x, y);
		velocity = v;
		this.addShape(new ConvexPolygon(10), Color.blue, Color.blue);
	}
	
	public void update(int delta) {
		velocity = physics.gravity(velocity, delta);
		translate(velocity.scale(delta));
	}
	
	public void explode() {
		//
	}
	
}
