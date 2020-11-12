import jig.Entity;
import jig.Vector;

public class tank extends Entity {
  //Constants
  public static final int INIT_TANK_HEALTH = 100;
  public static final float TANKMOVESPEED = .2f;

  //Class Variables
  private Vector velocity;
  private int health;
  private float speed;
  private cannon cannon;

  public tank(final float x, final float y){
    super(x,y);
    velocity = new Vector(0, 0);
    setHealth(INIT_TANK_HEALTH);
    speed = TANKMOVESPEED;
    cannon = new cannon(this.getX(), this.getY());
  }

  public void fire(int power){
    cannon.fire(power);
  }

  public void update(int delta){
    tile currentTile = onGround();
    velocity = physics.gravity(velocity, delta);
    velocity = physics.friction(velocity, delta, currentTile);
    translate(velocity.scale(delta));
    cannon.setX(this.getX());
    cannon.setY(this.getY());
  }

  public void jump(){
    if (onGround()){
      velocity = new Vector(velocity.getX(), -.5f);
    }
  }

  //Need to detect world under tank;NEED IMPLEMENTATION
  private tile onGround() {
    return null;
  }

  //set/get functions
  public void takeDmg(int dmg){ this.health -=dmg; }
  public int getHealth() {return health;}
  public void setHealth(int health) {this.health = health;}
}
