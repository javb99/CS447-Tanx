import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Image;

import java.util.function.Consumer;

public class Cannon extends Entity {
  //constants

  private static final float PROJECTILE_FIRE_OFFSET = 50;
  private static final float CANNON_SPRITE_SCALE = 3f;
  public static final float SPRITE_ROTATION_OFFSET = -90;
  public static final double ROTATION_SPEED = 100;
  public static final double MAX_ROTATION_FACTOR = 90;
  public static final double MIN_ROTATION_FACTOR = 0;
  public static final Vector BASE_CANNON_MOUNT = new Vector(-5, 12);



  public static final int BASE_CANNON = 0;
  public static final String BASE_CANNON_STR = "Basic Cannon";
  public static final float BASE_CANNON_POWER = 1f;
  public static final int BASE_CANNON_DAMAGE = 25;
  public static final int BASE_CANNON_RADIUS = 60;

  public static final int BIG_CANNON = 1;
  public static final String BIG_CANNON_STR = "Long Range Cannon";
  public static final float BIG_CANNON_POWER = 1.5f;
  public static final int BIG_CANNON_DAMAGE = 40;
  public static final int BIG_CANNON_RADIUS = 60;

  public static final int CLUSTER_CANNON = 2;
  public static final String CLUSTER_CANNON_STR = "Cluster Bomb";
  public static final float CLUSTER_CANNON_POWER = 1f;
  public static final int CLUSTER_CANNON_DAMAGE = 0;
  public static final int CLUSTER_CANNON_RADIUS = 0;

  public static final int MOUNTAIN_MAKER = 3;
  public static final String MOUNTAIN_MAKER_STR = "Mountain Maker";
  public static final float MOUNTAIN_MAKER_POWER = 1f;
  public static final int MOUNTAIN_MAKER_DAMAGE = 10;
  public static final int MOUNTAIN_MAKER_RADIUS = 100;
  
  public static final int ICE_BOMB = 4;
  public static final String ICE_BOMB_STR = "Ice Bomb";
  public static final float ICE_BOMB_POWER = 1f;
  public static final float ICE_BOMB_OFFSET = 50;
  public static final int ICE_BOMB_DAMAGE = 10;
  public static final int ICE_BOMB_RADIUS = 100;

  public static final int  FIRE_CLUSTER_CANNON = 5;
  public static final String FIRE_CLUSTER_CANNON_STR = "Fire Cluster Bomb";
  public static final float FIRE_CLUSTER_CANNON_POWER = 1f;
  public static final int FIRE_CLUSTER_CANNON_DAMAGE = 20;
  public static final int FIRE_CLUSTER_RADIUS = 0;

  //class variables
  private int type;
  private float power;
  private int damage;
  private int radius;
  private float fireOffset;
  private Image cannonSprite;
  private Image mirroredCannonSprite;
  private Image nonMirroredCannonSprite;
  private Vector cannonMountOffset;
  
  /// Degrees up from the tank's horizontal. When the tank is mirrored, this value doesn't change.
  private double rotationFactor;
  private double tankRotation;
  private boolean isMirrored;

  public Cannon(final float x, final float y, int type){
    super(x,y);
    rotationFactor = MIN_ROTATION_FACTOR;
    changeType(type);
    setRotationBasedOnTankAndRotationFactor();
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
	case ICE_BOMB:
		return ICE_BOMB_STR;
	case FIRE_CLUSTER_CANNON:
        return FIRE_CLUSTER_CANNON_STR;
	default:
		return null;
	}
  }

  public void changeType(int newType){
    type = newType;
    switch(newType) {
      case BASE_CANNON:
        power = BASE_CANNON_POWER;
        fireOffset = PROJECTILE_FIRE_OFFSET;
        damage = BASE_CANNON_DAMAGE;
        radius = BASE_CANNON_RADIUS;
        cannonMountOffset = BASE_CANNON_MOUNT;
        changeSprite(Tanx.BASE_CANNON_SPRITE);
        break;
      case BIG_CANNON:
        power = BIG_CANNON_POWER;
        fireOffset = PROJECTILE_FIRE_OFFSET;
        damage = BIG_CANNON_DAMAGE;
        radius = BIG_CANNON_RADIUS;
        cannonMountOffset = BASE_CANNON_MOUNT;
        changeSprite(Tanx.BASE_CANNON_SPRITE);
        break;
      case CLUSTER_CANNON:
        power = CLUSTER_CANNON_POWER;
        fireOffset = PROJECTILE_FIRE_OFFSET;
        damage = CLUSTER_CANNON_DAMAGE;
        radius = CLUSTER_CANNON_RADIUS;
        cannonMountOffset = BASE_CANNON_MOUNT;
        changeSprite(Tanx.BASE_CANNON_SPRITE);
        break;
      case MOUNTAIN_MAKER:
        power = MOUNTAIN_MAKER_POWER;
        fireOffset = PROJECTILE_FIRE_OFFSET;
        damage = MOUNTAIN_MAKER_DAMAGE;
        radius = MOUNTAIN_MAKER_RADIUS;
        cannonMountOffset = BASE_CANNON_MOUNT;
        changeSprite(Tanx.BASE_CANNON_SPRITE);
    	break;
    case ICE_BOMB:
    	power = ICE_BOMB_POWER;
    	fireOffset = ICE_BOMB_OFFSET;
    	damage = ICE_BOMB_DAMAGE;
        radius = ICE_BOMB_RADIUS;
        cannonMountOffset = BASE_CANNON_MOUNT;
        changeSprite(Tanx.BASE_CANNON_SPRITE);
        break;
      case FIRE_CLUSTER_CANNON:
        power = FIRE_CLUSTER_CANNON_POWER;
        fireOffset = PROJECTILE_FIRE_OFFSET;
        damage = FIRE_CLUSTER_CANNON_DAMAGE;
        radius = FIRE_CLUSTER_RADIUS;
        cannonMountOffset = BASE_CANNON_MOUNT;
        changeSprite(Tanx.BASE_CANNON_SPRITE);
        break;
    }
  }

  public void changeSprite(String sprite){
    nonMirroredCannonSprite = ResourceManager.getImage(sprite);
    nonMirroredCannonSprite = nonMirroredCannonSprite.getScaledCopy(CANNON_SPRITE_SCALE);
    nonMirroredCannonSprite.rotate(SPRITE_ROTATION_OFFSET);

    mirroredCannonSprite = nonMirroredCannonSprite.getFlippedCopy(false, true);
    mirroredCannonSprite.rotate(SPRITE_ROTATION_OFFSET);

    mirrorSpriteIfNeeded();
  }
  public void mirrorSpriteIfNeeded() {
    if (isMirrored) {
      changeSprite(mirroredCannonSprite);
    } else {
      changeSprite(nonMirroredCannonSprite);
    }
  }
  public void changeSprite(Image sprite){
    removeImage(cannonSprite);
    cannonSprite = sprite;
    addImage(cannonSprite);
  }

  /* This Method rotates the cannon with a set speed defined above
    The angle calculations are done in degrees
    ROTATIONSPEED should be in degrees per second
    */
  public void rotate(CannonDirection direction, int delta){
    double rotationAmount = ROTATION_SPEED * delta/1000;
    if (direction == CannonDirection.UP) {
      if (rotationFactor <= MAX_ROTATION_FACTOR){
        rotationFactor += rotationAmount;
      }
    } else {
      if (rotationFactor >= MIN_ROTATION_FACTOR){
        rotationFactor -= rotationAmount;
      }
    }
    setRotationBasedOnTankAndRotationFactor();
  }
  public void updateTankRotation(double rotation, boolean isMirrored) {
    this.tankRotation = rotation;
    this.isMirrored = isMirrored;
    setRotationBasedOnTankAndRotationFactor();
  }
  public void setRotationBasedOnTankAndRotationFactor() {
    if (isMirrored) {
      setRotation(-SPRITE_ROTATION_OFFSET + tankRotation - (180 - rotationFactor));
    } else {
      setRotation(-SPRITE_ROTATION_OFFSET + tankRotation - rotationFactor);
    }
    mirrorSpriteIfNeeded();
//    System.out.println("tank: " + tankRotation + ", cannon: " + rotationFactor + ", mirrored: " + isMirrored + " -> " + getRotation());
  }

  //input:float from 0 to 1 determing power strength
  //output: projectile of the cannon's type on firing
  //onError: outputs null projectile
  public void fire(float p, Consumer<Projectile> spawnP){
    System.out.println("Fired with: " + Float.toString(p) + " power!");
    if (power < 0) power = 0;
    float launchPower = p*power;
    double angle = Math.toRadians(getRotation() + SPRITE_ROTATION_OFFSET);
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
      case FIRE_CLUSTER_CANNON:
        spawnP.accept(new FireClusterProjectile(x, y, projVelocity, radius, damage, spawnP));
        break;
      case ICE_BOMB:
      	spawnP.accept(new IceBomb(x, y, projVelocity, radius, damage));
      	break;
      default:
        spawnP.accept(new Projectile(x, y, projVelocity, radius, damage));
        break;
    }
  }

  public int getType() { return type; }

  public void setMountPoint(Vector cannonMount) {
    // TODO: I think this should use the resolved rotation instead of the factor.
    
    //mount point is from center of cannon sprite
//    setPosition(cannonMount.add(cannonMountOffset.negate().rotate(rotationFactor)));
    setPosition(cannonMount.add(VectorMath.mirrorXIf(isMirrored, cannonMountOffset.negate()).rotate(getRotation())));
  }
}
class VectorMath {
  static Vector mirrorY(Vector input) {
    return input.setY(-input.getY());
  }
  static Vector mirrorYIf(boolean shouldMirror, Vector input) {
    if (!shouldMirror) { return input; }
    return input.setY(-input.getY());
  }
  static Vector mirrorX(Vector input) {
    return input.setX(-input.getX());
  }
  static Vector mirrorXIf(boolean shouldMirror, Vector input) {
    if (!shouldMirror) { return input; }
    return input.setX(-input.getX());
  }
}
