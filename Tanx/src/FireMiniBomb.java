import jig.Vector;

import java.util.function.Consumer;

public class FireMiniBomb extends Projectile {

    public static final float Y_SPAWN_OFFSET = -20;

    public FireMiniBomb(final float x, final float y, Vector v, int r, int d) {
        super(x, y, v, r, d);
    }
}
