import jig.Entity;
import jig.Vector;

public class PhysicEntity extends Entity {
  //constants

  //class variables
  private Vector acceleration;
  private Vector velocity;

  public PhysicEntity (final float x, final float y){
    super(x,y);
  }

  protected void setVelocity(Vector v){velocity = v;}
  protected void setAcceleration(Vector a){acceleration = a;}
  public Vector getAcceleration(){return acceleration;}
  public Vector getVelocity(){return velocity;}

}
