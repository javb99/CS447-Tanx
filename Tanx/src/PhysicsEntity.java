import jig.Entity;
import jig.Vector;

public class PhysicsEntity extends Entity {
  //constants

  //class variables
  protected boolean isDead;
  private Vector acceleration;
  private Vector velocity;
  private float drag;
  private float terminalX;
  private float terminalY;

  public PhysicsEntity (final float x, final float y, final float d, final float tx, final float ty){
    super(x,y);
    isDead = false;
    acceleration = new Vector(0, 0);
    velocity = new Vector(0, 0);
    drag = d;
    terminalX = tx;
    terminalY = ty;
  }
  
  public void update(int delta, Terrain t) {
	  /*
	   	basic physics entities should probably do nothing,
	  	this method is just here for the more specific physics entities to inherit
	  	and allow for generalized calling by the engine
	  */
  }

  protected void setVelocity(Vector v){velocity = v;}
  protected void setAcceleration(Vector a){acceleration = a;}
  public Vector getAcceleration(){return acceleration;}
  public Vector getVelocity(){return velocity;}
  public float getDrag(){return drag;}
  public float getTerminalX() {return terminalX;}
  public float getTerminalY() {return terminalY;}
  public boolean getIsDead() { return isDead; };
}
