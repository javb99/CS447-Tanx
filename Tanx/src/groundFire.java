import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Animation;

public class groundFire extends PhysicsEntity {
  public static int FIRE_DAMAGE_PER_TURN = 10;
  public static int TURNS_ON_FIRE = 3;
  public static float FIRE_SOUND_VOLUME = .5f;
  public static float FIRE_SOUND_PITCH = 1f;
  public static float FIRE_SCALE = 1.5f;
  public static Vector FIRE_TERMINAL_VELOCITY = new Vector(2f, 2f);

  Animation fireAnim;

  public groundFire(final float x, final float y) {
    super (x, y, 0, FIRE_TERMINAL_VELOCITY);
    fireAnim = new Animation(ResourceManager.getSpriteSheet(Tanx.FIRE_DEBUFF, 32, 32),
        0, 0, 3, 3, true, 100, true);
    addAnimation(fireAnim);
    fireAnim.setLooping(true);
    fireAnim.start();
    setScale(FIRE_SCALE);
  }

  public void applyFire(Tank t) {
    ResourceManager.getSound(Tanx.FIRE_DEBUFF_SND).play(FIRE_SOUND_PITCH, FIRE_SOUND_VOLUME);
    isDead = true;
    t.applyFire(TURNS_ON_FIRE, this);
  }
}
