import java.util.LinkedList;

import jig.Entity;
import jig.Shape;
import jig.Vector;

public class PhysicsEntity extends Entity {
  //constants

  //class variables
  protected boolean isDead;
  private Vector acceleration;
  private Vector velocity;
  private float drag;
  private Vector terminal;

  public PhysicsEntity (final float x, final float y, final float d, final Vector t){
    super(x,y);
    isDead = false;
    acceleration = new Vector(0, 0);
    velocity = new Vector(0, 0);
    drag = d;
    terminal = t;
  }
  
  /**
    Check for collision with the terrain. This method is specific to each entity because of
    differing entity shapes. The default implementation checks the coarse-grained bounding circle
    before delegating to shouldResolveFineGrainedTerrainCollision.
   */
  public boolean shouldResolveTerrainCollision(Terrain terrain, int delta) {
    
    Vector position = getPosition();
    float radius = getCoarseGrainedRadius();
    
    boolean isCoarseCollision = terrain.checkCircularCollision(position, radius);
    if (!isCoarseCollision) { return false; }
    
    return shouldResolveFineGrainedTerrainCollision(terrain, delta);
  }
  
  /// Default implementation only supports rectangles, everything else uses the coarse-grained check.
  public boolean shouldResolveFineGrainedTerrainCollision(Terrain terrain, int delta) {
    boolean hasRectangle = false;
    LinkedList<Shape> shapes = getShapes();
    for(Shape s : shapes) {
      if (s.getPointCount() == 4) {
        hasRectangle = true;
        if(terrain.checkRectangularCollision(new Vector(s.getMinX(), s.getMinY()), new Vector(s.getMaxX(), s.getMaxY()))) {
          return true;
        }
      }
    }
    return !hasRectangle;
  }

  protected void setVelocity(Vector v){velocity = v;}
  protected void setAcceleration(Vector a){acceleration = a;}
  public Vector getAcceleration(){return acceleration;}
  public Vector getVelocity(){return velocity;}
  public float getDrag(){return drag;}
  public Vector getTerminal() {return terminal;}
  public boolean getIsDead() { return isDead; };
}
