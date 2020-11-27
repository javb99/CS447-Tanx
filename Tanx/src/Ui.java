import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.pushingpixels.substance.api.colorscheme.OliveColorScheme;
import org.w3c.dom.css.Rect;

import java.util.ArrayList;

public class Ui {
  InFocusUi inCamUi;
  OutFocusUi outCamUi;

  public Ui(Rectangle bottomBounds, Vector bottomPostions){
    inCamUi = new InFocusUi();
    outCamUi = new OutFocusUi(bottomBounds, bottomPostions);
  }

  public void renderInCam(Graphics g){
    inCamUi.render(g);
  }

  public void renderOutCam(Graphics g){
    outCamUi.render(g);
  }

  public void update(int delta, ArrayList<Player> players, Player currentPlayer){
    inCamUi.update();
    outCamUi.update(delta, currentPlayer);
  }

  public void initPlayerUi(ArrayList<Player> players) {
    //inCamUi.initTankHealthBars(players);
  }
}

class InFocusUi {
  //ArrayList<HealthBar> tankHealthBars;

  public InFocusUi(){}

  public void render(Graphics g){
    //for (HealthBar h: tankHealthBars) { h.render(g); }
  }

/*  public void initTankHealthBars(ArrayList<Player> players){
    tankHealthBars = new ArrayList<HealthBar>();
    for (Player p: players){
      for (Tank t: p.getTanks()){
        tankHealthBars.add(new HealthBar(t.getPosition(), Tank.MAX_TANK_HEALTH, t));
      }
    }
  }*/

  public void update(){
/*    for (HealthBar h: tankHealthBars) {
      Tank t = (Tank) h.getOwner();
      h.setPosition(t.getPosition());
      h.setValue(t.getHealth());
      h.update();
    }*/
  }
}

class OutFocusUi {
  BottomUi bottomUi;

  public OutFocusUi(Rectangle bottomUiBounds, Vector bottomUiPosition){
    bottomUi = new BottomUi(bottomUiBounds, bottomUiPosition);
  }

  public void render(Graphics g){
    bottomUi.render(g);
  }

  public void update(int delta, Player player){
    bottomUi.update(delta, player);
  }
}

class BottomUi extends UiContainer{
  final float JET_ELEMENT_MIN_ANGLE = -120f;
  final float JET_ELEMENT_MAX_ANGLE = 120f;
  final Vector fuelPosOffset = new Vector(100, 250);
  final Vector weaponPosOffset = new Vector(200, 200);
  GaugeElement jetFuelElement;
  WeaponSelect weaponSelect;

  public BottomUi(Rectangle bounds, Vector pos){
    super(bounds, pos);
    Entity uiBack = new Entity(position.getX(), position.getY());
    uiBack.addShape(new ConvexPolygon(bounds.getWidth(), bounds.getHeight()), Color.black, Color.red);
    addEntity(uiBack, position);
    Vector fuelPos = position.add(fuelPosOffset);
    Vector weaponPos = position.add(weaponPosOffset);
    jetFuelElement = new GaugeElement(fuelPos, Tank.INIT_FUEL_BURNTIME,
        JET_ELEMENT_MIN_ANGLE, JET_ELEMENT_MAX_ANGLE,Tanx.FUEL_GAUGE_OVERLAY, Tanx.Fuel_GAUGE_ARROW);
    weaponSelect = new WeaponSelect(weaponPos);
  }

  @Override
  public void render(Graphics g) {
    super.render(g);
    jetFuelElement.render(g);
    weaponSelect.render(g);
  }

  public void update(int delta, Player player) {
    jetFuelElement.setValue(player.getTank().getFuel());
    weaponSelect.update(delta, player);
  }
}

class UiContainer {
  protected Vector position;
  protected Rectangle containerBounds;
  protected ArrayList<Entity> entities;
  protected ArrayList<UiContainer> uiContainers;

  public UiContainer(Rectangle bounds, Vector pos){
    position = pos;
    containerBounds = bounds;
    entities = new ArrayList<Entity>();
    uiContainers = new ArrayList<UiContainer>();
  }

  public void addEntity(Entity e, Vector relPosition){
    entities.add(e);
    e.setPosition(position.add(relPosition));
  }

  public void removeEntity(Entity e) {
    if (entities.contains(e)) {
      entities.remove(e);
    } else {
      System.out.println("UiContainer.removeEntityERROR: Tried to remove entity that did not exist in container!");
    }
  }

  public void setContainerBounds(Rectangle r) {
    containerBounds = r;
  }

  public void render(Graphics g){
    for (Entity e: entities){ e.render(g); }
    for (UiContainer u: uiContainers) { u.render(g); }
  }
}

class WeaponSelect {
  final float Y_OFFSET = 20f;
  final Vector POINTER_OFFSET = new Vector(-10, 10);
  private Vector position;
  private Entity pointer;
  private ArrayList<Ammo> ammoList;
  private int currentAmmo;

  public WeaponSelect(Vector pos) {
    ammoList = new ArrayList<Ammo>();
    position = pos;
    pointer = new Entity(0, 0);
    pointer.addImage(ResourceManager.getImage(Tanx.WEAPON_POINTER));
  }

  public void update(int delta, Player player){
    ammoList = player.getAmmoList();
    currentAmmo = player.getCurrentAmmo();
  }

  public void render(Graphics g) {
    for(int index = 0; index < ammoList.size(); index++) {
      String wepString = Cannon.getTypeStr(ammoList.get(index).type) + " ";
      if (ammoList.get(index).amount == Ammo.INF_AMMO) {
        wepString += "INF";
      } else {
        wepString += Integer.toString((ammoList.get(index).amount));
      }
      g.drawString(wepString, position.getX(), position.getY() + Y_OFFSET*index);
      if (currentAmmo == ammoList.get(index).type){
        pointer.setPosition(position.add(POINTER_OFFSET));
        pointer.setY(pointer.getY() + Y_OFFSET*index);
        pointer.render(g);
      }
    }
  }

}

class HealthBar extends StatusBarElement{
  final Vector BAR_OFFSET = new Vector(0, 40);
  final float SCALE = .5f;
  final float BAR_MAX_WIDTH = 100f;
  final float BAR_MAX_HEIGHT = 30f;
  private Entity overlay;
  private Entity underLay;
  private Entity bar;
  private Entity owner;
  private Vector barOffset;

  public HealthBar(Vector pos, float maxHealth, Entity own){
    super(pos, maxHealth);
    owner = own;
    overlay = new Entity(pos.getX(), pos.getY());
    overlay.addImage(ResourceManager.getImage(Tanx.HEALTH_BAR));
    overlay.setScale(SCALE);
    underLay = new Entity(pos.getX(), pos.getY());
    underLay.addShape(new ConvexPolygon(BAR_MAX_WIDTH, BAR_MAX_HEIGHT), Color.black, Color.black);
    underLay.setScale(SCALE);
    bar = new Entity(pos.getX(), pos.getY());
    bar.setScale(SCALE);
    barOffset = new Vector(0,0);
  }

  @Override
  public void setValue(float val) {
    super.setValue(val);
    changeBarSize();
  }

  @Override
  public void setPosition(Vector p) {
    super.setPosition(p.add(BAR_OFFSET));
  }

  public void update(){
    underLay.setPosition(position);
    overlay.setPosition(position);
    bar.setPosition(position.add(barOffset));
  }

  private void changeBarSize() {
    ConvexPolygon barShape;
    while (!bar.getShapes().isEmpty()){
      bar.removeShape(bar.getShapes().getFirst());
    }
    float barWidth = BAR_MAX_WIDTH*(1 - value/maxValue);
    float barXoffset = (BAR_MAX_WIDTH/2 - barWidth/2)*SCALE;
    barOffset = new Vector(barXoffset, 0);
    barShape = new ConvexPolygon(barWidth, BAR_MAX_HEIGHT);
    bar.addShape(barShape, Color.black, Color.black);
  }

  public void render(Graphics g){
    underLay.render(g);
    overlay.render(g);
    bar.render(g);
  }

  public Entity getOwner() { return owner; }
}

class GaugeElement extends StatusBarElement {
  private float maxAngle;
  private float minAngle;
  private float angle;
  private Entity arrow;
  private Entity overlay;

  public GaugeElement(Vector pos, float maxVal, float minA, float maxA, String overLaySprite, String arrowSprite){
    super(pos, maxVal);
    setMaxAngle(maxA);
    setMinAngle(minA);
    arrow = new Entity(0,0);
    arrow.addImage(ResourceManager.getImage(arrowSprite));
    overlay = new Entity(0,0);
    overlay.addImage(ResourceManager.getImage(overLaySprite));
    setPosition(pos);
  }

  public void changeAngle() {
    float full = maxAngle + Math.abs(minAngle);
    float ratio = value/maxValue;
    angle = minAngle + full*ratio;
    arrow.setRotation(angle);
  }

  public void render(Graphics g) {
    overlay.render(g);
    arrow.render(g);
  }

  @Override
  public void setValue(float val) {
    super.setValue(val);
    changeAngle();
  }

  @Override
  public void setPosition(Vector p) {
    super.setPosition(p);
    arrow.setPosition(position);
    overlay.setPosition(position);
  }

  private void setMinAngle(float minA) { minAngle = minA; }
  private void setMaxAngle(float maxA) { maxAngle = maxA; }
}

class StatusBarElement {
  protected Vector position;
  public float value;
  public float maxValue;

  public StatusBarElement(Vector pos, float maxVal){
    position = pos;
    value = 0;
    maxValue = maxVal;
  }

  public void add(float amount){
    amount += amount;
    checkValue();
  }

  public void sub(float amount) {
    amount -= amount;
    checkValue();
  }

  private void checkValue() {
    if (value > maxValue) { value = maxValue; }
    if (value < 0) { value = 0; }
  }

  public void setValue(float val) {
    value = val;
    checkValue();
  }

  public void setPosition(Vector p){
    position = p;
  }

  public float getMaxValue() { return maxValue; }

  public void setMaxValue(float maxValue) { this.maxValue = maxValue; }

  public float getValue() { return value; }
}
