import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

enum Direction {LEFT, RIGHT};

public class Tank extends PhysicsEntity {
  //Constants
  public static final float INF_HEALTH = -9999;
  public static final int INIT_TANK_HEALTH = 100;
  public static final int MAX_TANK_HEALTH = 100;
  public static final float TANK_MOVE_SPEED = .2f;
  public static final float TANK_TERMINAL_VELOCITY = 2f;
  public static final float ACCELERATION = .05f;
  public static final Vector ACCELERATION_JETS = new Vector(0, -.0015f);
  public static final float JET_OFFSET_Y = 40f;

  //Class Variables
  private Cannon cannon;
  private boolean onGround;
  private Player myPlayer;
  private Healthbar healthbar;
  private boolean invuln;
  private Effect jumpJetsEffect;
  private int jumpJetsCD;


  public Tank(final float x, final float y, Color c, Player player){
    super(x,y, 0, new Vector(TANK_MOVE_SPEED, TANK_TERMINAL_VELOCITY));
    setVelocity(new Vector(0, 0));
    setAcceleration(new Vector(0,0));

    healthbar = new Healthbar(INIT_TANK_HEALTH);
    cannon = new Cannon(x, y, Cannon.BASE_CANNON);
    myPlayer = player;
    this.addShape(new ConvexPolygon(64f, 32f), c, Color.red);
    invuln = false;
    jumpJetsEffect = new Effect(x, y, new Animation(
        ResourceManager.getSpriteSheet(Tanx.FIRE_ANIMATION, 32, 32),
        0, 0, 3, 3, true, 50, true));
    jumpJetsEffect.setRotation(180);
    jumpJetsEffect.setSound(Tanx.JET_SOUND, 150, .2f, .5f);
  }

  public Projectile fire(float power){
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
    setVelocity(getVelocity().add(ACCELERATION_JETS.scale(delta)));
    jumpJetsCD = 100;
    jumpJetsEffect.turnOnSound();
  }

  public void update(int delta){
    jumpJetsCD -= delta;
    if (jumpJetsCD > 0) {
      jumpJetsEffect.update(delta);
    } else {
      jumpJetsEffect.turnOffSound();
    }
  }
  
  @Override
  public void render(Graphics g) {
    super.render(g);
    if (jumpJetsCD > 0){
      jumpJetsEffect.render(g, getX(), getY() + JET_OFFSET_Y);
    }
    cannon.render(g, getX(), getY());
    float bottomSpacing = 20;
    healthbar.render(g, this.getCoarseGrainedMaxY() + bottomSpacing, this.getX());
  }
  public void changeWeapon(int type){
    cannon.changeType(type);
  }

  
  public void giveHealth(int amount) {
    healthbar.receiveHealth(amount);
  }
  public void takeDamage(int amount) {
    if (!invuln) healthbar.receiveDamage(amount);
  }
  @Override
  public boolean getIsDead() {
    return healthbar.getIsDead();
  }
  
  //set/get functions
  public void setOnGround(boolean onGround) { this.onGround = onGround; }
  public boolean isOnGround() { return onGround; }
  public Player getMyPlayer() { return myPlayer; }

  //tank cheat handlers
  public void toggleInfHealth() {
    invuln = !invuln;
  }

  public boolean isInfHealth() {
    return invuln;
  }

  public void killTank() {
    healthbar.receiveDamage(healthbar.health);
  }
}