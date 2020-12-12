import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.function.Consumer;

public class Cannon extends Entity {
  //constants
  public static float ROTATION_SPEED = 100;
  public static float MAX_ROTATION_FACTOR = 90;
  public static float ANGLE_CORRECTION = -90;

  public static int BASE_CANNON = 0;
  public static String BASE_CANNON_STR = "Basic Cannon";
  public static float BASE_CANNON_POWER = 1f;
  public static float BASE_CANNON_OFFSET = 50;
  public static int BASE_CANNON_DAMAGE = 20;
  public static int BASE_CANNON_RADIUS = 30;

  public static int BIG_CANNON = 1;
  public static String BIG_CANNON_STR = "Long Range Cannon";
  public static float BIG_CANNON_POWER = 1f;
  public static float BIG_CANNON_OFFSET = 50;
  public static int BIG_CANNON_DAMAGE = 50;
  public static int BIG_CANNON_RADIUS = 60;

  public static int CLUSTER_CANNON = 2;
  public static String CLUSTER_CANNON_STR = "Cluster Bomb";
  public static float CLUSTER_CANNON_POWER = 1f;
  public static float CLUSTER_CANNON_OFFSET = 50;
  public static int CLUSTER_CANNON_DAMAGE = 0;
  public static int CLUSTER_CANNON_RADIUS = 0;

  //class variables
  private int type;
  private float power;
  private int damage;
  private int radius;
  private float fireOffset;
  private float rotationFactor;

  public Cannon(final float x, final float y, int type){
    super(x,y);
    changeType(type);
    this.addShape(new ConvexPolygon(10f, 45f), Color.red, Color.blue);
  }

  public static String getTypeStr(int type) {
    if (type == BIG_CANNON) { return BIG_CANNON_STR;
    } else if (type == BASE_CANNON) { return BASE_CANNON_STR;
    } else if (type == CLUSTER_CANNON) { return CLUSTER_CANNON_STR;
    } else { return null; }
  }

  public void changeType(int newType){
    type = newType;
    if (newType == BASE_CANNON){
      power = BASE_CANNON_POWER;
      fireOffset = BASE_CANNON_OFFSET;
      damage = BASE_CANNON_DAMAGE;
      radius = BASE_CANNON_RADIUS;
      //changeSprite(Tanx.BASIC_CANNON_SPRITE);
    } else if (newType == BIG_CANNON){
      power = BIG_CANNON_POWER;
      fireOffset = BIG_CANNON_OFFSET;
      damage = BIG_CANNON_DAMAGE;
      radius = BIG_CANNON_RADIUS;
      //changeSprite(tanx.BIG_CANNON_SPRITE);
    } else if (newType == CLUSTER_CANNON) {
      power = CLUSTER_CANNON_POWER;
      fireOffset = CLUSTER_CANNON_OFFSET;
      damage = CLUSTER_CANNON_DAMAGE;
      radius = CLUSTER_CANNON_RADIUS;
      //changeSprite(tanx.CLUSTER_CANNON_SPRITE);
    }
  }

  public void changeSprite(String sprite){
    //removeImage(ResourceManager.getImage(Tanx.BASIC_CANNON_SPRITE));
    //removeImage(ResourceManager.getImage(Tanx.BIG_CANNON_SPRITE));
    //removeImage(ResourceManager.getImage(Tanx.CLUSTER_CANNON_SPRITE));
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
    double angle = Math.toRadians(rotationFactor + ANGLE_CORRECTION);
    Vector projVelocity = new Vector((float)Math.cos(angle), (float)Math.sin(angle));
    projVelocity = projVelocity.setLength(launchPower);
    float x = getX() + fireOffset*(float)Math.cos(angle);
    float y = getY() + fireOffset*(float)Math.sin(angle);
    if (type == CLUSTER_CANNON) {
      spawnP.accept(new ClusterProjectile(x, y, projVelocity, radius, damage, spawnP));
    } else {
      spawnP.accept(new Projectile(x, y, projVelocity, radius, damage));
    }

  }

  public int getType() { return type; }
}
