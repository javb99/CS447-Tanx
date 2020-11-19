import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Player {
  ArrayList<Tank> tanks;
  int tankIndex;
  public boolean isDead;
  Color playerColor;
  //tbd weapons available to player listed here?

  public Player(Color c) {
    tanks = new ArrayList<Tank>();
    tankIndex = 0;
    isDead = false;
    playerColor = c;
  }

  public void render(Graphics g){
    for (Tank tank : tanks) {
      tank.render(g);
    }
  }

  public void addTank(float x, float y) {
    tanks.add(new Tank(x, y, playerColor));
  }

  public void removeTank(Tank t) {
    tanks.remove(t);
  }

  public Tank getTank(int index){
    if (tanks.isEmpty()) {
      return null;
    }
    return tanks.get(index);
  }
  public Tank getNextTank() {
    tankIndex++;
    if (tankIndex >= tanks.size()) {
      tankIndex = 0;
    }
    return tanks.get(tankIndex);
  }

  public Tank getPrevTank() {
    tankIndex--;
    if (tankIndex <= 0){
      tankIndex = tanks.size() - 1;
    }
    return getTank(tankIndex);
  }

  public int tanksLeft(){return tanks.size();}
}
