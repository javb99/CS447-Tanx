import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import org.newdawn.slick.Color;

import java.util.ArrayList;

public class Cannon extends Entity {
  //constants
  public static boolean LEFT = false;
  public static boolean RIGHT = true;
  public static float ROTATIONSPEED = 1;
  public static float MAXROTATIONFACTOR = 90;
  public static int BASECANNON = 0;
  public static int BASECANNON_POWER = 100;
  public static float BASECANNON_OFFSET = 0;
  //class variables
  private int type;
  private int power;
  private float rotationFactor;//rotationFactor describes how far the cannon is pointed from the tank's normal vector
  ArrayList<Integer> test;

  public Cannon(final float x, final float y){
    super(x,y);
    type = BASECANNON;
    power = BASECANNON_POWER;
    this.addShape(new ConvexPolygon(10f, 45f), Color.red, Color.blue);
  }

  public void changeType(int newType){
    type = newType;
    if (newType == BASECANNON){
      power = BASECANNON_POWER;
      changeSprite(Tanx.BASIC_CANNON_SPRITE);
    }
  }

  public void changeSprite(String sprite){
    removeImage(ResourceManager.getImage(Tanx.BASIC_CANNON_SPRITE));
    addImage(ResourceManager.getImage(sprite));
  }

  //NEEDS PROJECTILE IMPLEMENTATION
  //public void fire(int power, ArrayList<projectile> p){}

  /* This Method rotates the cannon with a set speed defined above
  The angle calculations are done in degrees
  ROTATIONSPEED should be in degrees per second
  */
  public void rotate(int delta, Direction direction){
    float rotationAmount = ROTATIONSPEED*delta/1000;
    if (direction == Direction.LEFT) {
      rotationFactor += rotationAmount;
      if (Math.abs(rotationFactor) > MAXROTATIONFACTOR){
        rotationFactor = MAXROTATIONFACTOR;
      }
    } else {
      rotationFactor -= rotationAmount;
      if (Math.abs(rotationFactor) > MAXROTATIONFACTOR){
        rotationFactor = -MAXROTATIONFACTOR;
      }
    }
  }
}
