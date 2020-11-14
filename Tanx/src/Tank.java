import jig.ConvexPolygon;
import jig.Vector;
import org.newdawn.slick.Color;

enum Direction {LEFT, RIGHT};

public class Tank extends PhysicsEntity {
  //Constants
  public static final int INIT_TANK_HEALTH = 100;
  public static final float TANKMOVESPEED = .2f;
  public static final float ACCELERATION = .05f;
  public static final float JUMPSPEED = .5f;

  //Class Variables
  private int health;
  private float speed;
  private Cannon cannon;
  private boolean onGround;

  public Tank(final float x, final float y){
    super(x,y);
    setHealth(INIT_TANK_HEALTH);
    speed = TANKMOVESPEED;
    cannon = new Cannon(this.getX(), this.getY());
    this.addShape(new ConvexPolygon(64f, 32f), Color.blue, Color.red);
  }

  //public void fire(int power){cannon.fire(power);}
  public void rotate(Direction direction, int delta){cannon.rotate(delta, direction);}

  public void update(int delta){
    cannon.setX(this.getX());
    cannon.setY(this.getY());
  }

  public void move(Direction direction){
    if (direction == Direction.LEFT){
      setAcceleration(new Vector(-ACCELERATION, getAcceleration().getY()));
    } else {
      setAcceleration(new Vector(ACCELERATION, getAcceleration().getY()));
    }
  }

  public void jump(){
    if (onGround){
      setVelocity(new Vector(getVelocity().getX(), JUMPSPEED));
    }
  }


  //set/get functions
  public void takeDmg(int dmg){ this.health -= dmg; }
  public int getHealth() {return health;}
  public void setHealth(int health) {this.health = health;}
  public void setOnGround(boolean onGround) {this.onGround = onGround;}
  public boolean isOnGround() {return onGround;}
}
