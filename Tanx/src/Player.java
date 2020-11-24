import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import java.util.ArrayList;

public class Player {
  private ArrayList<Tank> tanks;
  private int tankIndex;
  public boolean isDead;
  private Color playerColor;
  private int playerId;
  //tbd weapons available to player listed here?

  public Player(Color c, int id) {
    tanks = new ArrayList<Tank>();
    tankIndex = 0;
    isDead = false;
    playerColor = c;
    playerId = id;
  }

  public void render(Graphics g){
    for (Tank tank : tanks) {
      tank.render(g);
    }
  }

  public Tank addTank(float x, float y) {
	Tank t = new Tank(x, y, playerColor, this);
    tanks.add(t);
    isDead = false;
    return t;
  }

  public void removeTank(Tank t) {
    if (tanks.contains(t)){
      tanks.remove(t);
      if (tanks.isEmpty()){
        isDead = true;
      }
    } else {
      System.out.println("removeTankERROR: Tank not in this player!");
    }
  }

  public Tank getTank(int index){
    if (tanks.isEmpty()) {
      System.out.println("getTankERROR: No more tanks to get!");
      return null;
    }
    return tanks.get(index);
  }
  public Tank getTank() {
    return getTank(tankIndex);
  }

  public void getNextTank() {
    tankIndex++;
    if (tankIndex >= tanks.size()) {
      tankIndex = 0;
    }
  }

  public void getPrevTank() {
    tankIndex--;
    if (tankIndex < 0){
      tankIndex = tanks.size() - 1;
    }
  }

  public void update(int delta){
    for (Tank t: tanks){t.update(delta);}
  }

  public int tanksLeft(){return tanks.size();}

  public boolean isDead() { return isDead; }

  public ArrayList<Tank> getTanks() { return tanks; }
}
