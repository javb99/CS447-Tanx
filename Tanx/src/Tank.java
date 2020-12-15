import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.util.function.Consumer;

enum Direction {LEFT, RIGHT, NONE};

public class Tank extends PhysicsEntity {
  //Constants
  public static final float INF_HEALTH = -9999;
  public static final int INIT_TANK_HEALTH = 100;
  public static final int MAX_TANK_HEALTH = 100;
  public static final float TANK_MOVE_SPEED = .2f;
  public static final float TANK_TERMINAL_VELOCITY = 2f;
  public static final float ACCELERATION = .75f;
  public static final float FRICTION = 0.12f;
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

  private double targetRotation = 0;
  
  static boolean showDebugRays = false;
  private RayPair debugTerrainBoundaryRays[];
  private int debugShortestRaysIndexes[];
  private Vector debugTerrainNormal;
  private Vector debugFriction;

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
    this.addShape(new ConvexPolygon(points));
    
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
    if (direction == Direction.LEFT) {
      activeTankSprite = leftTankSprite;
      setAcceleration(new Vector(-ACCELERATION, 0).rotate(getRotation()));
    } else if (direction == Direction.RIGHT) {
      activeTankSprite = rightTankSprite;
      setAcceleration(new Vector(ACCELERATION, 0).rotate(getRotation()));
    } else {
      setAcceleration(new Vector(0, 0));
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
    takeDamage(GroundFire.FIRE_DAMAGE_PER_TURN);
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
    
    this.rotate(this.velocityToward(clampDouble(targetRotation, -90, 90), 0.3, delta));
  }
  
  @Override
  public void render(Graphics g) {
    if (showDebugRays) {
      renderDebugRays(g);
      return;
    }
    super.render(g);
    g.pushTransform();
    g.rotate(getX(), getY(), (float) getRotation()); 
    g.drawImage(activeTankSprite, getX() - activeTankSprite.getWidth()/2, getY() - activeTankSprite.getHeight()/2, myPlayer.getColor());
    g.popTransform();
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
  private void renderDebugRays(Graphics g) {
    this.topEdge().translate(new Vector(0, spriteHeight()).rotate(getRotation())).draw(g, Color.green);

    if (debugTerrainNormal != null) {
     LineSegment.offset(getPosition(), debugTerrainNormal.setLength(80)).draw(g, Color.pink);

      for (int i : debugShortestRaysIndexes) {
       if (i == -1) {
         continue;
       }
       this.debugTerrainBoundaryRays[i].draw(g, Color.red);
     }
   }
   LineSegment v = LineSegment.offset(getPosition(), this.getVelocity().scale(1000));
   v.draw(g, Color.lightGray);
   if (debugFriction != null) {
     LineSegment f = LineSegment.offset(getPosition(), this.debugFriction.scale(1000));
     f.draw(g, Color.red);
   }
   LineSegment a = LineSegment.offset(getPosition(), this.getAcceleration().scale(1000));
   a.draw(g, Color.green);

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
  
  @Override
  public boolean shouldResolveTerrainCollision(Terrain terrain, int delta) {
    calculateTranslation(delta, terrain);
    return false;
  }
  
  private void calculateTranslation(int delta, Terrain terrain) {
    int downwardRayCount = 3;
    
    RayPair terrainBoundaryRays[] = this.calculateTerrainRays(terrain, downwardRayCount);
    this.debugTerrainBoundaryRays = terrainBoundaryRays;
    
    int shortestRaysIndexes[] = indexesOfShortest(terrainBoundaryRays);
    Vector shortestNormals[] = new Vector[2];
    Vector terrainPoints[] = new Vector[2];
    
    for (int i = 0; i < shortestRaysIndexes.length; i++) {
      int rayIndex = shortestRaysIndexes[i];
      if (rayIndex == -1) {
        continue;
      }
      Vector normal = terrainBoundaryRays[rayIndex].surfaceNormal();
      terrainPoints[i] =  terrainBoundaryRays[rayIndex].surface().center();
      shortestNormals[i] = normal;
    }
    this.debugShortestRaysIndexes = shortestRaysIndexes;

    Vector terrainNormal = new LineSegment(terrainPoints[1], terrainPoints[0]).unitNormalSpecial();
    this.debugTerrainNormal = terrainNormal;
    
    if (shortestRaysIndexes[0] == -1) {
      return; // no rays at all.
    }
    RayPair maxPenetrationRay = terrainBoundaryRays[shortestRaysIndexes[0]];
    float distanceToTerrain = maxPenetrationRay.avgLength() - spriteHeight();
    
    if (distanceToTerrain < 0) {
      this.translate(maxPenetrationRay.first.getDirection().scale(distanceToTerrain));
      this.applyFriction(delta, terrainNormal);
      this.setVelocity(this.getVelocity().project(terrainNormal.getPerpendicular()));
      this.rotateToNormal(terrainNormal);
    }
  }
  private int[] indexesOfShortest(RayPair lines[]) {
    int k = 2;
    int bestIndexes[] = new int[] { -1, -1 };
    float bestLengthSqs[] = new float[] { Float.MAX_VALUE, Float.MAX_VALUE };
    for (int i = 0; i < k; i++) { bestIndexes[i] = -1; }
    for (int i = 0; i < lines.length; i++) {
      float lengthSq = lines[i].avgLengthSquared();
      if (bestIndexes[0] == -1) {
        bestIndexes[0] = i;
        bestLengthSqs[0] = lengthSq;
      } else if (lengthSq < bestLengthSqs[0]) {
        bestIndexes[1] = bestIndexes[0];
        bestLengthSqs[1] = bestLengthSqs[0];
        bestIndexes[0] = i;
        bestLengthSqs[0] = lengthSq;
      } else if (bestIndexes[1] == -1) {
        bestIndexes[1] = i;
        bestLengthSqs[1] = lengthSq;
      } else if (lengthSq < bestLengthSqs[1]) {
        bestIndexes[1] = i;
        bestLengthSqs[1] = lengthSq;
      }
    }
    return bestIndexes;
  }
  private void rotateToNormal(Vector slopeNormal) {
    Vector vertical = new Vector(0, -1);
    double terrainAngle = Math.acos(slopeNormal.dot(vertical)) * 180.0/Math.PI;

    if(vertical.dot(slopeNormal.getPerpendicular()) > 0) {
      terrainAngle = -terrainAngle;
    }
    
    this.targetRotation = terrainAngle;
  }
  
  private void applyFriction(int delta, Vector terrainNormal) {
    Vector vNormal = this.getVelocity().project(terrainNormal);
    Vector vParallel = this.getVelocity().project(terrainNormal.getPerpendicular());
    float normalVelocityFactor = vNormal.length();
    Vector friction = vParallel.negate().setLength(FRICTION*normalVelocityFactor*delta).clampLength(0, vParallel.length());
    this.setVelocity(this.getVelocity().add(friction));
    debugFriction = friction;
  }


  private RayPair[] calculateTerrainRays(Terrain terrain, int downwardRayCount) {
    RayPair terrainBoundaryRays[] = new RayPair[downwardRayCount];
    LineSegment tankTop = this.topEdge();
    Vector step = tankTop.getDirection().scale(spriteWidth()/(downwardRayCount-1));
    Vector current = tankTop.start;
    Vector start = tankTop.start;
    for (int i = 0; i < downwardRayCount; i++) {
      current = start.add(step.scale(i));
      terrainBoundaryRays[i] = terrain.surfaceDistanceRays(current, tankTop.getDirection().getPerpendicular());
    }
    return terrainBoundaryRays;
  }
  
  
  private double velocityToward(double angle, double velocity, int delta) {
    double diff = angle - this.getRotation();
    double rotationVelocity = 0;
    if (diff > 0) {
      rotationVelocity = Math.min(velocity*delta, diff);
    } else if (diff < 0) {
      rotationVelocity = Math.max(-velocity*delta, diff);
    }
    return rotationVelocity;
  }
  
  private float spriteWidth() {
    return 25*TANK_SPRITE_SCALE;//activeTankSprite.getWidth();
  }
  private float spriteHeight() {
    return 23*TANK_SPRITE_SCALE;//activeTankSprite.getHeight();
  }
  
  private LineSegment topEdge() {
    Vector tankHorizontal = Vector.getUnit(this.getRotation());
    Vector tankCenter = getPosition();
    Vector tankTopFromCenter = tankHorizontal.getPerpendicular().scale(-spriteWidth()/2);

    Vector tankBottomLeft = tankCenter.subtract(tankHorizontal.scale(spriteWidth()/2));
    Vector tankBottomRight = tankCenter.add(tankHorizontal.scale(spriteWidth()/2));
    return new LineSegment(tankBottomLeft, tankBottomRight).translate(tankTopFromCenter);
  }
  
  
  private double clampDouble(double value, double min, double max) {
    double v = value;
    if (v < min) {
      v = min;
    }
    if (v > max) {
      v = max;
    }
    return v;
  }
}