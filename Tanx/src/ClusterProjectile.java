import jig.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

public class ClusterProjectile extends Projectile{
  public static int MINI_BOMB_DAMAGE = 20;
  public static int MINI_BOMB_RADIUS = 30;
  public static float MINI_BOMB_SPEED = .2f;
  public static int CLUSTER_SPLIT_TIME = 1000;
  public static int NUM_MINI_BOMBS = 4;
  public static Vector NORM = new Vector(0, 1).setLength(MINI_BOMB_SPEED);


  private int splitTime;
  private ArrayList<MiniBomb> miniBombs;
  Consumer<Projectile> projectileSpawner;


  public ClusterProjectile(final float x, final float y, Vector v, int r, int d, Consumer<Projectile> spawnP) {
    super(x,y, v, r, d);
    projectileSpawner = spawnP;
    splitTime = CLUSTER_SPLIT_TIME;
    TI = Projectile.TerrainInteraction.BASIC;
    miniBombs = new ArrayList<MiniBomb>();
  }

  @Override
  public void update(int delta) {
    super.update(delta);
    splitTime -= delta;
    if (splitTime <= 0) {
      explode();
    }
  }

  @Override
  public void explode() {
    Random rand = new Random();
    int upperBound = 360;
    super.explode();
    for (int i = 0; i < NUM_MINI_BOMBS; i++) {
      Vector velocity = NORM;
      velocity = velocity.rotate(rand.nextInt(upperBound));
      MiniBomb newBomb = new MiniBomb(getX(), getY(), velocity, MINI_BOMB_RADIUS, MINI_BOMB_DAMAGE, this);
      miniBombs.add(newBomb);
      float offset = getCoarseGrainedRadius();
      newBomb.translate(velocity.setLength(offset));
      projectileSpawner.accept(newBomb);
    }
  }
  
  public ArrayList<MiniBomb> getBombList(){
	  return miniBombs;
  }
}
