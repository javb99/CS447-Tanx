import java.util.ArrayList;

import jig.Vector;

public class PhysicsEngine {
	
	static public float GRAV_CONSTANT = 2f;
	static public float NORMAL_FRICTION = .1f;
	
	
	private ArrayList<PhysicsEntity> objects;
	//private World world;
	
	
	public PhysicsEngine(ArrayList<PhysicsEntity> o/*, World w*/) {
		objects = o;
		//world = w;
	}
	
	public void addPhysicsEntity(PhysicsEntity e) {
		objects.add(e);
	}
	
	public void update(int delta) {
		
		objects.forEach((n) -> applyPhysics(n, delta));
	
	}
	
	private void applyPhysics(PhysicsEntity e, int delta) {	//still needs to handle collisions
		Vector A = e.getAcceleration();	//get movement acceleration
		//System.out.println("incoming");
		//System.out.println(A);
		A = applyGravity(A);	//add gravity to acceleration
		//System.out.println("gravity");
		//System.out.println(A);
		//System.out.println("friction");
		//System.out.println(A);
		applyAccelerationtoVelocity(e, delta, A);	//change velocity
		applyTerminalVelocity(e);	//truncate if greater than terminal velocity
		translateEntity(e);	//move the object
	}
	
	private Vector applyGravity(Vector a) {
		a = a.setY(a.getY() + PhysicsEngine.GRAV_CONSTANT);
		return a;
	}
	
	
	//This function needs to be reworked, as the current implementation is not physically correct,
	//and today is friday, and i am ready to be done.
	/*private Vector applyFriction(Vector a, float friction) {
		float newA = a.length() - friction;
		if(newA < 0) {
			newA = 0;
		}
		a = a.setLength(newA);
		System.out.println(a);
		return a;
	}*/
	
	private void applyAccelerationtoVelocity(PhysicsEntity e, int delta, Vector a) {
		Vector v = e.getVelocity();
		v = v.add(a.scale(delta/1000f));
		e.setVelocity(v);
	}
	
	private void applyTerminalVelocity(PhysicsEntity e) {
		Vector v = e.getVelocity();
		float x = v.getX();
		float y = v.getY();
		
		if(x > e.getTerminalX()) {
			x = e.getTerminalX();
		}
		
		if(y > e.getTerminalY()) {
			y = e.getTerminalY();
		}
		
		e.setVelocity(new Vector(x, y));
	}
	
	private void translateEntity(PhysicsEntity e) {
		e.translate(e.getVelocity());
	}
}
