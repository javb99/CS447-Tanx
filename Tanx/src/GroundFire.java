import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class GroundFire extends PhysicsEntity {
  public static int FIRE_DAMAGE_PER_TURN = 10;
  public static int TURNS_ON_FIRE = 3;
  public static float FIRE_SOUND_VOLUME = .5f;
  public static float FIRE_SOUND_PITCH = 1f;
  public static float FIRE_SCALE = 1.5f;
  public static float FIRE_TURN_SCALAR = .2f;
  public static Vector FIRE_TERMINAL_VELOCITY = new Vector(2f, 2f);
  public static int FIRE_DURATION = 5;
  public static float MELT_RADIUS = 60;

  private Animation fireAnim;
  private float currentScale;
  private int turnsAlive;

  public GroundFire(final float x, final float y) {
    super (x, y, 0, FIRE_TERMINAL_VELOCITY);
    this.addShape(new ConvexPolygon(28f, 28f));
    fireAnim = new Animation(ResourceManager.getSpriteSheet(Tanx.FIRE_DEBUFF, 32, 32),
        0, 0, 3, 3, true, 100, true);
    addAnimation(fireAnim);
    fireAnim.setLooping(true);
    fireAnim.start();
    currentScale = FIRE_SCALE;
    setScale(currentScale);
    turnsAlive = FIRE_DURATION;
    
  }

  public void applyFire(Tank t) {
    ResourceManager.getSound(Tanx.FIRE_DEBUFF_SND).play(FIRE_SOUND_PITCH, FIRE_SOUND_VOLUME);
    setDead(true);
    t.applyFire(TURNS_ON_FIRE, this);
  }

  public void render(Graphics g, Vector position) {
    setPosition(position);
    render(g);
  }

  public void updateTurn(Terrain t) {
    if (turnsAlive > 0) {
      turnsAlive--;
      currentScale -= FIRE_TURN_SCALAR;
      setScale(currentScale);
      t.changeTerrainInCircle(this.getPosition(), MELT_RADIUS, Terrain.TerrainType.ICE, Terrain.TerrainType.NORMAL, false);
    } else {
      setDead(true);
    }
  }
}

