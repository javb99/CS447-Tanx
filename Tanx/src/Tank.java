import jig.ConvexPolygon;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

enum Direction {LEFT, RIGHT};

public class Tank extends PhysicsEntity {
  //Constants
  public static final int INIT_TANK_HEALTH = 90;
  public static final int MAX_TANK_HEALTH = 100;
  public static final float INIT_FUEL_BURNTIME = 2*1000;
  public static final float TANK_MOVE_SPEED = .2f;
  public static final float TANK_TERMINAL_VELOCITY = 2f;
  public static final float ACCELERATION = .05f;
  public static final Vector ACCELERATION_JETS = new Vector(0, -.0015f);

  //Tank Stats
  private int health;
  private float fuel;
  private Cannon cannon;
  private boolean onGround;
  private Player myPlayer;


  public Tank(final float x, final float y, Color c, Player player){
    super(x,y, 0, new Vector(TANK_MOVE_SPEED, TANK_TERMINAL_VELOCITY));
    setVelocity(new Vector(0, 0));
    setAcceleration(new Vector(0,0));

    setHealth(INIT_TANK_HEALTH);
    cannon = new Cannon(x, y, Cannon.BASE_CANNON);
    myPlayer = player;
    this.addShape(new ConvexPolygon(64f, 32f), c, Color.red);
  }

  public Projectile fire(int power){
    myPlayer.giveAmmo(cannon.getType(), -1);
    return cannon.fire(power);
  }

  public void rotate(Direction direction, int delta){cannon.rotate(direction, delta);}

  public void move(Direction direction){
    if (direction == Direction.LEFT){
      setAcceleration(new Vector(-ACCELERATION, getAcceleration().getY()));
    } else {
      setAcceleration(new Vector(ACCELERATION, getAcceleration().getY()));
    }
  }

  public void jet(int delta){
    if (fuel > 0){
      setVelocity(getVelocity().add(ACCELERATION_JETS.scale(delta)));
      fuel -= delta;
    }
  }

  public void update(int delta){
    health -= 1;
    if (health <= 0){health = MAX_TANK_HEALTH; }
  }
  
  @Override
  public void render(Graphics g) {
    super.render(g);
    cannon.setX(this.getX());
    cannon.setY(this.getY());
    cannon.render(g);
  }
  public void changeWeapon(int type){
    cannon.changeType(type);
  }

  public void giveHealth(int amount) {
    if (amount <= 0){System.out.println("Tank.giveHealthERROR: Attempted to give 0 or below health to a tank!"); return;}
    health += amount;
    if (health > MAX_TANK_HEALTH){
      health = MAX_TANK_HEALTH;
    }
  }

  //set/get functions
  public void takeDmg(int dmg){ this.health -= dmg; }
  public int getHealth() { return health; }
  public void setHealth(int health) { this.health = health; }
  public void setOnGround(boolean onGround) { this.onGround = onGround; }
  public boolean isOnGround() { return onGround; }
  public Player getMyPlayer() { return myPlayer; }

  public void setFuel(float fuel) { this.fuel = fuel; }
  public float getFuel() { return fuel; }
  public int getFuelPercentage() {return (int)(fuel/INIT_FUEL_BURNTIME*100);}
}