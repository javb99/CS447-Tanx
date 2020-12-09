import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Vector;
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

  //Class Variables
  private Cannon cannon;
  private boolean onGround;
  private Player myPlayer;
  private Healthbar healthbar;
  private boolean invuln;
  private int turnsOnFire;
  private groundFire fireDebuff;


  public Tank(final float x, final float y, Color c, Player player){
    super(x,y, 0, new Vector(TANK_MOVE_SPEED, TANK_TERMINAL_VELOCITY));
    setVelocity(new Vector(0, 0));
    setAcceleration(new Vector(0,0));

    healthbar = new Healthbar(INIT_TANK_HEALTH);
    cannon = new Cannon(x, y, Cannon.BASE_CANNON);
    myPlayer = player;
    this.addShape(new ConvexPolygon(64f, 32f), c, Color.red);
    invuln = false;
    turnsOnFire = 0;
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
  }

  public void update(int delta){ }

  public void updateTurn() {
    System.out.println(turnsOnFire);
    if ( turnsOnFire > 0 ) {
      turnsOnFire --;
      ResourceManager.getSound(Tanx.FIRE_DEBUFF_SND).play(groundFire.FIRE_SOUND_PITCH, groundFire.FIRE_SOUND_VOLUME);
      takeDamage(groundFire.FIRE_DAMAGE_PER_TURN);
    }
  }
  
  @Override
  public void render(Graphics g) {
    super.render(g);
    cannon.setX(this.getX());
    cannon.setY(this.getY());
    cannon.render(g);
    float bottomSpacing = 20;
    healthbar.render(g, this.getCoarseGrainedMaxY() + bottomSpacing, this.getX());
    if (turnsOnFire > 0 && fireDebuff != null) {
      fireDebuff.setPosition(getPosition());
      fireDebuff.render(g);
    }
  }
  public void changeWeapon(int type){
    cannon.changeType(type);
  }

  public void applyFire(int turns, groundFire fire) {
    turnsOnFire = turns;
    System.out.println("TurnsSet: " + turns + " turnsOnFire: " + turnsOnFire);
    fireDebuff = fire;
  }

  //health functions
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