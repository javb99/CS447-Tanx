import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;


import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.function.Consumer;

public class Cannon extends Entity {
  //constants

  private static final float PROJECTILE_FIRE_OFFSET = 50;
  private static final float CANNON_SPRITE_SCALE = 3f;
  public static final float SPRITE_ROTATION_OFFSET = -90;
  public static final float ROTATION_SPEED = 100;
  public static final float MAX_ROTATION_FACTOR = 90;
  public static final float ANGLE_CORRECTION = -90;
  public static final Vector BASE_CANNON_MOUNT = new Vector(-5, 12);



  public static final int BASE_CANNON = 0;
  public static final String BASE_CANNON_STR = "Basic Cannon";
  public static final float BASE_CANNON_POWER = 1f;
  public static final float BASE_CANNON_OFFSET = 50;
  public static final int BASE_CANNON_DAMAGE = 20;
  public static final int BASE_CANNON_RADIUS = 30;

  public static final int BIG_CANNON = 1;
  public static final String BIG_CANNON_STR = "Long Range Cannon";
  public static final float BIG_CANNON_POWER = 1f;
  public static final float BIG_CANNON_OFFSET = 50;
  public static final int BIG_CANNON_DAMAGE = 50;
  public static final int BIG_CANNON_RADIUS = 60;

  public static final int CLUSTER_CANNON = 2;
  public static final String CLUSTER_CANNON_STR = "Cluster Bomb";
  public static final float CLUSTER_CANNON_POWER = 1f;
  public static final float CLUSTER_CANNON_OFFSET = 50;
  public static final int CLUSTER_CANNON_DAMAGE = 0;
  public static final int CLUSTER_CANNON_RADIUS = 0;
  
  public static final int MOUNTAIN_MAKER = 3;
  public static final String MOUNTAIN_MAKER_STR = "Mountain Maker";
  public static final float MOUNTAIN_MAKER_POWER = 1f;
  public static final float MOUNTAIN_MAKER_OFFSET = 50;
  public static final int MOUNTAIN_MAKER_DAMAGE = 10;
  public static final int MOUNTAIN_MAKER_RADIUS = 100;

  //class variables
  private int type;
  private float power;
  private int damage;
  private int radius;
  private float fireOffset;
  private float rotationFactor;
  private Image cannonSprite;
  private Vector cannonMountOffset;

  public Cannon(final float x, final float y, int type){
    super(x,y);
    rotationFactor = MAX_ROTATION_FACTOR;
    rotate(MAX_ROTATION_FACTOR);
    changeType(type);
    //this.addShape(new ConvexPolygon(10f, 45f), Color.red, Color.blue);
  }

  public static String getTypeStr(int type) {

	switch(type) {
	case BIG_CANNON:
		return BIG_CANNON_STR;
	case BASE_CANNON:
		return BASE_CANNON_STR;
	case CLUSTER_CANNON:
		return CLUSTER_CANNON_STR;
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
        damage = BASE_CANNON_DAMAGE;
        radius = BASE_CANNON_RADIUS;
        cannonMountOffset = BASE_CANNON_MOUNT;
        changeSprite(Tanx.BASE_CANNON_SPRITE);
    	break;
    case BIG_CANNON:
    	power = BIG_CANNON_POWER;
        fireOffset = BIG_CANNON_OFFSET;
        damage = BIG_CANNON_DAMAGE;
        radius = BIG_CANNON_RADIUS;
        cannonMountOffset = BASE_CANNON_MOUNT;
        changeSprite(Tanx.BASE_CANNON_SPRITE);
    	break;
    case CLUSTER_CANNON:
    	power = CLUSTER_CANNON_POWER;
        fireOffset = CLUSTER_CANNON_OFFSET;
        damage = CLUSTER_CANNON_DAMAGE;
        radius = CLUSTER_CANNON_RADIUS;
        cannonMountOffset = BASE_CANNON_MOUNT;
        changeSprite(Tanx.BASE_CANNON_SPRITE);
        break;
    case MOUNTAIN_MAKER:
    	power = MOUNTAIN_MAKER_POWER;
    	fireOffset = MOUNTAIN_MAKER_OFFSET;
    	damage = MOUNTAIN_MAKER_DAMAGE;
        radius = MOUNTAIN_MAKER_RADIUS;
        cannonMountOffset = BASE_CANNON_MOUNT;
        changeSprite(Tanx.BASE_CANNON_SPRITE);
    	break;
    }
  }

  public void changeSprite(String sprite){
    removeImage(cannonSprite);
    cannonSprite = ResourceManager.getImage(sprite);
    cannonSprite = cannonSprite.getScaledCopy(CANNON_SPRITE_SCALE);
    cannonSprite.rotate(SPRITE_ROTATION_OFFSET);
    addImage(cannonSprite);
  }

  /* This Method rotates the cannon with a set speed defined above
    The angle calculations are done in degrees
    ROTATIONSPEED should be in degrees per second
    */
  public void rotate(Direction direction, int delta){
    float rotationAmount = ROTATION_SPEED *delta/1000;
    if (direction == Direction.RIGHT) {
      if (rotationFactor <= MAX_ROTATION_FACTOR){
        rotationFactor += rotationAmount;
        rotate(rotationAmount);
      }
    } else {
      if (rotationFactor >= -MAX_ROTATION_FACTOR){
        rotationFactor -= rotationAmount;
        rotate(-rotationAmount);
      }
    }
  }

  public void render(Graphics g, final float x, final float y) {
    setPosition(x, y);
    super.render(g);
  }

  //input:float from 0 to 1 determing power strength
  //output: projectile of the cannon's type on firing
  //onError: outputs null projectile
  public void fire(float p, Consumer<Projectile> spawnP){
    System.out.println("Fired with: " + Float.toString(p) + " power!");
    if (power < 0) power = 0;
    float launchPower = p*power;
    double angle = Math.toRadians(getRotation() + ANGLE_CORRECTION);
    Vector projVelocity = new Vector((float)Math.cos(angle), (float)Math.sin(angle));
    projVelocity = projVelocity.setLength(launchPower);

    float x = getX() + fireOffset*(float)Math.cos(angle);
    float y = getY() + fireOffset*(float)Math.sin(angle);
    
    switch(type) {
    case CLUSTER_CANNON:
    	spawnP.accept(new ClusterProjectile(x, y, projVelocity, radius, damage, spawnP));
    	break;
    case MOUNTAIN_MAKER:
    	spawnP.accept(new MountainMaker(x, y, projVelocity, radius, damage));
    	break;
    default:
    	spawnP.accept(new Projectile(x, y, projVelocity, radius, damage));
    	break;
    }
  }

  public int getType() { return type; }

  public void setMountPoint(Vector cannonMount) {
    //mount point is from center of cannon sprite
    setPosition(cannonMount.add(cannonMountOffset.negate().rotate(rotationFactor)));
  }
}
