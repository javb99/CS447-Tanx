import jig.Entity;
import jig.Vector;

public class PhysicsEntity extends Entity {
  //constants

  //class variables
  private Vector acceleration;
  private Vector velocity;
  private float drag;
  private float terminalX;
  private float terminalY;

  public PhysicsEntity (final float x, final float y, final float d, final float tx, final float ty){
    super(x,y);
    drag = d;
    terminalX = tx;
    terminalY = ty;
  }

  protected void setVelocity(Vector v){velocity = v;}
  protected void setAcceleration(Vector a){acceleration = a;}
  public Vector getAcceleration(){return acceleration;}
  public Vector getVelocity(){return velocity;}
  public float getDrag(){return drag;}
  public float getTerminalX() {return terminalX;}
  public float getTerminalY() {return terminalY;}
  
}
