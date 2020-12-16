import jig.Vector;

import java.util.Random;
import java.util.function.Consumer;

public class FireClusterProjectile extends Projectile {
    public static int MINI_BOMB_DAMAGE = 0;
    public static int MINI_BOMB_RADIUS = 0;
    public static float MINI_BOMB_SPEED = .5f;
    public static int CLUSTER_SPLIT_TIME = 1000;
    public static Vector NORM = new Vector(0, -1).setLength(MINI_BOMB_SPEED);
    private int splitTime;
    private Consumer<Projectile> projectileSpawner;

    public FireClusterProjectile(final float x, final float y, Vector v, int r, int d, Consumer<Projectile> spawnP) {
        super(x, y, v, r, d);
        splitTime = CLUSTER_SPLIT_TIME;
        projectileSpawner = spawnP;
    }

    @Override
    public void update(int delta) {
        super.update(delta);
        splitTime -= delta;
        if (splitTime <= 0) {
            explode(getVelocity());
        }
    }

    @Override
    public void explode() {
        super.explode();
        explode(NORM);
    }

    public void explode(Vector angleVector) {
        double angles[] = new double[]{-30, 30, 0};
        super.explode();
        for (double angle : angles) {
            Vector velocity = angleVector;
            velocity = velocity.rotate(angle);

            Projectile newBomb = new FireMiniBomb(getX(), getY(), velocity, MINI_BOMB_RADIUS, MINI_BOMB_DAMAGE);

            float offset = getCoarseGrainedRadius();
            newBomb.translate(velocity.setLength(offset));
            projectileSpawner.accept(newBomb);
        }
    }
}
