import jig.Vector;

public class physics {
  //constants
  static public float GRAVCONSTANT = .05f;
  static public float TERMINALVELOCITY = .5f;
  public static float NORMALFRICTION = .1f;


  public static Vector gravity(Vector v, int delta){
    float y = v.getY();
    y += delta/1000*GRAVCONSTANT;
    return new Vector(v.getX(), y);
  }

  public static Vector friction(Vector v, int delta, int tileType){
    float friction = NORMALFRICTION;
    float x = v.getX();
    if (x < 0){
      x += delta*1000*friction;
      if (x > 0) x = 0;
    } else if (x > 0) {
      x -= delta*1000*friction;
      if (x < 0) x = 0;
    }
    return new Vector(x, v.getY());
  }
}
