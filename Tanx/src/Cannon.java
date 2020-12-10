import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Color;

public class Cannon extends Entity {
  //constants
  public static final float ROTATION_SPEED = 100;
  public static final float MAX_ROTATION_FACTOR = 90;
  public static final float ANGLE_CORRECTION = -90;
  public static final int BASE_CANNON = 0;
  public static final String BASE_CANNON_STR = "Basic Cannon";
  public static final float BASE_CANNON_POWER = 1f;
  public static final float BASE_CANNON_OFFSET = 50;
  public static final int BIG_CANNON = 1;
  public static final String BIG_CANNON_STR = "Long Range Cannon";
  public static final float BIG_CANNON_POWER = 1f;
  public static final float BIG_CANNON_OFFSET = 50;
  public static final int MOUNTAIN_MAKER = 2;
  public static final String MOUNTAIN_MAKER_STR = "Mountain Maker";
  public static final float MOUNTAIN_MAKER_POWER = 1f;
  public static final float MOUNTAIN_MAKER_OFFSET = 50;
  //class variables
  private int type;
  private float power;
  private float fireOffset;
  private float rotationFactor;

  public Cannon(final float x, final float y, int type){
    super(x,y);
    changeType(type);
    this.addShape(new ConvexPolygon(10f, 45f), Color.red, Color.blue);
  }

  public static String getTypeStr(int type) {
	switch(type) {
	case BIG_CANNON:
		return BIG_CANNON_STR;
	case BASE_CANNON:
		return BASE_CANNON_STR;
	case MOUNTAIN_MAKER:
		return MOUNTAIN_MAKER_STR;
	default:
		return null;
	}
    
  }

  public void changeType(int newType){
    type = newType;
    switch(newType) {
    case BASE_CANNON:
    	power = BASE_CANNON_POWER;
        fireOffset = BASE_CANNON_OFFSET;
        //changeSprite(Tanx.BASIC_CANNON_SPRITE);
    	break;
    case BIG_CANNON:
    	power = BIG_CANNON_POWER;
        fireOffset = BIG_CANNON_OFFSET;
        //changeSprite(tanx.BIG_CANNON_SPRITE);
    	break;
    case MOUNTAIN_MAKER:
    	power = MOUNTAIN_MAKER_POWER;
    	fireOffset = MOUNTAIN_MAKER_OFFSET;
    	break;
    }
   
  }

  public void changeSprite(String sprite){
    //removeImage(ResourceManager.getImage(Tanx.BASIC_CANNON_SPRITE));
    //removeImage(ResourceManager.getImage(Tanx.BIG_CANNON_SPRITE));
    addImage(ResourceManager.getImage(sprite));
  }

  /* This Method rotates the cannon with a set speed defined above
  The angle calculations are done in degrees
  ROTATIONSPEED should be in degrees per second
  */
  public void rotate(Direction direction, int delta){
    float rotationAmount = ROTATION_SPEED *delta/1000;
    if (direction == Direction.RIGHT) {
      rotationFactor += rotationAmount;
      if (Math.abs(rotationFactor) > MAX_ROTATION_FACTOR){
        rotationFactor = MAX_ROTATION_FACTOR;
      }
    } else {
      rotationFactor -= rotationAmount;
      if (Math.abs(rotationFactor) > MAX_ROTATION_FACTOR){
        rotationFactor = -MAX_ROTATION_FACTOR;
      }
    }
    setRotation(rotationFactor);
  }

  //input:float from 0 to 1 determing power strength
  //output: projectile of the cannon's type on firing
  //onError: outputs null projectile
  public Projectile fire(float p){
    System.out.println("Fired with: " + Float.toString(p) + " power!");
    if (power < 0) power = 0;
    float launchPower = p*power;
    double angle = Math.toRadians(rotationFactor + ANGLE_CORRECTION);
    Vector projVelocity = new Vector((float)Math.cos(angle), (float)Math.sin(angle));
    projVelocity = projVelocity.setLength(launchPower);
    float x = getX() + fireOffset*(float)Math.cos(angle);
    float y = getY() + fireOffset*(float)Math.sin(angle);
    switch(type) {
    case MOUNTAIN_MAKER:
    	return new MountainMaker(x, y, projVelocity);
    default:
    	return new Projectile(x, y, projVelocity);
    }
  }

  public int getType() { return type; }
}
