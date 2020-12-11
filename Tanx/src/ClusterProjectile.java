import jig.Vector;
//import org.pushingpixels.substance.api.colorscheme.OliveColorScheme;

import java.util.function.Consumer;

public class ClusterProjectile extends Projectile{
  public static int MINI_BOMB_DAMAGE = 20;
  public static int MINI_BOMB_RADIUS = 10;
  public static float MINI_BOMB_SPEED = .01f;
  public static int CLUSTER_SPLIT_TIME = 1000;
  public static Vector NORM = new Vector(0, 1).setLength(MINI_BOMB_SPEED);


  private int splitTime;
  Consumer<Projectile> projectileSpawner;


  public ClusterProjectile(final float x, final float y, Vector v, int r, int d, Consumer<Projectile> spawnP) {
    super(x,y, v, r, d);
    projectileSpawner = spawnP;
    splitTime = CLUSTER_SPLIT_TIME;
    TI = Projectile.TerrainInteraction.BASIC;
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
    super.explode();
    Vector v1 = NORM;
    Vector v2 = v1;
    Vector v3 = v1;
    Vector v4 = v1;
    v1 = v1.rotate(45);
    v2 = v1.rotate(90);
    v3 = v2.rotate(90);
    v4 = v3.rotate(90);
    Projectile p1 = new Projectile(getX(), getY(), v1, MINI_BOMB_RADIUS, MINI_BOMB_DAMAGE);
    Projectile p2 = new Projectile(getX(), getY(), v2, MINI_BOMB_RADIUS, MINI_BOMB_DAMAGE);
    Projectile p3 = new Projectile(getX(), getY(), v3, MINI_BOMB_RADIUS, MINI_BOMB_DAMAGE);
    Projectile p4 = new Projectile(getX(), getY(), v4, MINI_BOMB_RADIUS, MINI_BOMB_DAMAGE);
    float offset = getCoarseGrainedRadius();
    p1.translate(v1.setLength(offset));
    p2.translate(v2.setLength(offset));
    p3.translate(v3.setLength(offset));
    p4.translate(v4.setLength(offset));
    projectileSpawner.accept(p1);
    projectileSpawner.accept(p2);
    projectileSpawner.accept(p3);
    projectileSpawner.accept(p4);
  }
}
