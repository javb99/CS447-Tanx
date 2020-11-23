import jig.ConvexPolygon;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

enum Direction {LEFT, RIGHT};

public class Tank extends PhysicsEntity {
  //Constants
  public static final int INIT_TANK_HEALTH = 100;
  public static final float TANK_MOVE_SPEED = .2f;
  public static final float ACCELERATION = .05f;
  public static final float JUMP_SPEED = .5f;

  //Class Variables
  private int health;
  private Cannon cannon;
  private boolean onGround;

  public Tank(final float x, final float y){
    super(x,y, 0, new Vector(100, 100));
    setHealth(INIT_TANK_HEALTH);
    cannon = new Cannon(this.getX(), this.getY());
    this.addShape(new ConvexPolygon(64f, 32f), Color.blue, Color.red);
  }

  public Projectile fire(int power){return cannon.fire(power);}
  public void rotate(Direction direction, int delta){cannon.rotate(direction, delta);}

  public void move(Direction direction){
    if (direction == Direction.LEFT){
      setAcceleration(new Vector(-ACCELERATION, getAcceleration().getY()));
    } else {
      setAcceleration(new Vector(ACCELERATION, getAcceleration().getY()));
    }
  }

  public void jump(){
    if (onGround){
      setVelocity(new Vector(getVelocity().getX(), JUMP_SPEED));
    }
  }

  public void update(int delta){
    cannon.setX(this.getX());
    cannon.setY(this.getY());
  }
  
  @Override
  public void render(Graphics g) {
    super.render(g);
    cannon.render(g);
  }

  //set/get functions
  public void takeDmg(int dmg){ this.health -= dmg; }
  public int getHealth() {return health;}
  public void setHealth(int health) {this.health = health;}
  public void setOnGround(boolean onGround) {this.onGround = onGround;}
  public boolean isOnGround() {return onGround;}
}