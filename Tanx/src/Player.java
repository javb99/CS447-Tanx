import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;

import java.util.*;

public class Player {
  private ArrayList<Tank> tanks;
  private int tankIndex;
  public boolean isDead;
  private Color playerColor;
  private int playerId;
  private ArrayList<Ammo> ammo;
  private int ammoIndex;
  //tbd weapons available to player listed here?

  public Player(Color c, int id) {
    tanks = new ArrayList<Tank>();
    tankIndex = 0;
    isDead = false;
    playerColor = c;
    playerId = id;
    ammo = new ArrayList<Ammo>();
    giveAmmo(Cannon.BASE_CANNON, Ammo.INF_AMMO);
    giveAmmo(Cannon.BIG_CANNON, 10);
    ammoIndex = 0;
  }

  public void render(Graphics g){
    for (Tank tank : tanks) {
      tank.render(g);
    }
  }

  public void giveAmmo(int type, int amount) {
    for (Ammo a: ammo ){
      if (a.type == type){
        if(a.amount == Ammo.INF_AMMO) { return; }
        a.amount += amount;
        return;
      }
    }
    ammo.add(new Ammo(type, amount));
  }

  private void newAmmoIndex(int amount){
    ammoIndex += amount;
    if (ammoIndex >= ammo.size()){
      ammoIndex = 0;
    } else if (ammoIndex < 0){
      ammoIndex = ammo.size() - 1;
    }
    if (ammo.get(ammoIndex).amount == 0){ newAmmoIndex(amount); }
  }
  public void nextWeapon(){
    newAmmoIndex(1);
    tanks.get(tankIndex).changeWeapon(ammo.get(ammoIndex).type);
  }

  public void prevWeapon(){
    newAmmoIndex(-1);
    tanks.get(tankIndex).changeWeapon(ammo.get(ammoIndex).type);
  }

  public void checkWeapon(){
    if (ammo.get(ammoIndex).amount == 0){
      nextWeapon();
    } else {
      tanks.get(tankIndex).changeWeapon(ammo.get(ammoIndex).type);
    }
  }

  public void addTank(float x, float y) {
    tanks.add(new Tank(x, y, playerColor, this));
    isDead = false;
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

  public int getAmmo(){return ammo.get(ammoIndex).amount;}
}
