import jig.Entity;

import java.util.ArrayList;

public class cannon extends Entity {
  //constants
  public static boolean LEFT = false;
  public static boolean RIGHT = true;
  public static int BASECANNON = 0;
  public static int BASECANNON_POWER = 100;
  public static float BASECANNON_OFFSET = 0;
  //class variables
  private int type;
  private int power;
  ArrayList<Integer> test;

  public cannon(final float x, final float y){
    super(x,y);
    type = BASECANNON;
    power = BASECANNON_POWER;
  }

  public void changeType(int newType){
    type = newType;
  }

  public void fire(int power, arrayList<projectile> p){
    projectile newP = new projectile(x, y, v, t);
    p.add(newP);
  }

  public void rotate(int delta, boolean direction){}
}
