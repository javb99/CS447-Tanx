import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.util.function.Consumer;

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
  public static final float TANK_SPRITE_SCALE = 3f;
  private static final Vector TANK_MOUNT_OFFSET = new Vector(15, 0);
  public static final float JET_OFFSET_Y = 40f;

  //Class Variables
  private Cannon cannon;
  private boolean onGround;
  private Player myPlayer;
  private Healthbar healthbar;
  private boolean invuln;
  private Image activeTankSprite;
  private Image leftTankSprite;
  private Image rightTankSprite;
  private Effect jumpJetsEffect;
  private int jumpJetsCD;
  private int onFireTurns;
  private GroundFire fireDebuffEntity;


  public Tank(final float x, final float y, Color c, Player player){
    super(x,y, 0, new Vector(TANK_MOVE_SPEED, TANK_TERMINAL_VELOCITY));
    setVelocity(new Vector(0, 0));
    setAcceleration(new Vector(0,0));

    healthbar = new Healthbar(INIT_TANK_HEALTH);
    cannon = new Cannon(x, y, Cannon.BASE_CANNON);
    myPlayer = player;
    
   
    rightTankSprite = ResourceManager.getImage(Tanx.TANK_SPRITE);
    rightTankSprite.setImageColor(c.r, c.g, c.b);
    rightTankSprite = rightTankSprite.getScaledCopy(TANK_SPRITE_SCALE);
    leftTankSprite = rightTankSprite.getFlippedCopy(true, false);
    activeTankSprite = rightTankSprite;
    
    Vector[] points = new Vector[6];
    points[0] = new Vector(-35, 5);
    points[1] = new Vector(-35, 30);
    points[2] = new Vector(35, 30);
    points[3] = new Vector(35, 5);
    points[4] = new Vector(15, -15);
    points[5] = new Vector(-15, -15);
    this.addShape(new ConvexPolygon(points), Color.green, Color.green);
    
    invuln = false;
    jumpJetsEffect = new Effect(x, y, new Animation(
        ResourceManager.getSpriteSheet(Tanx.FIRE_ANIMATION, 32, 32),
        0, 0, 3, 3, true, 50, true));
    jumpJetsEffect.setRotation(180);
    jumpJetsEffect.setSound(Tanx.JET_SOUND, 150, .2f, .5f);

    onFireTurns = 0;
  }


  public void fire(float power, Consumer<Projectile> spawnP){
    myPlayer.giveAmmo(cannon.getType(), -1);
    cannon.fire(power, spawnP);
  }

  public void rotate(Direction direction, int delta){cannon.rotate(direction, delta);}

  public void move(Direction direction){
    if (direction == Direction.LEFT){
      activeTankSprite = leftTankSprite;
      setAcceleration(new Vector(-ACCELERATION, getAcceleration().getY()));
    } else {
      activeTankSprite = rightTankSprite;
      setAcceleration(new Vector(ACCELERATION, getAcceleration().getY()));
    }
  }

  public void jet(int delta){
    setVelocity(getVelocity().add(ACCELERATION_JETS.scale(delta)));
    jumpJetsCD = 100;
    jumpJetsEffect.turnOnSound();
  }

  public void applyFire(int turnsOnFire, GroundFire groundFire) {
    onFireTurns = turnsOnFire;
    fireDebuffEntity = groundFire;
    takeDamage(groundFire.FIRE_DAMAGE_PER_TURN);
  }

  public void updateTurn() {
    if (onFireTurns > 0 ) {
      onFireTurns--;
      takeDamage(GroundFire.FIRE_DAMAGE_PER_TURN);
    }
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
    g.drawImage(activeTankSprite, getX() - activeTankSprite.getWidth()/2, getY() - activeTankSprite.getHeight()/2, myPlayer.getColor());
    Vector cannonMount = TANK_MOUNT_OFFSET.rotate(getRotation()).add(getPosition());
    cannon.setMountPoint(cannonMount);
    cannon.render(g);
    if (jumpJetsCD > 0){
      jumpJetsEffect.render(g, getX(), getY() + JET_OFFSET_Y);
    }
    if (onFireTurns > 0) {
      fireDebuffEntity.render(g, getPosition());
    }
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