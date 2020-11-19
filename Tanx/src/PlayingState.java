import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.Color;

import jig.Entity;
import jig.Vector;

enum phase {MOVEFIRE, FIRING};

public class PlayingState extends BasicGameState {
  static private int TURNLENGTH = 10*1000;
	World world;
	DebugCamera camera;
  ArrayList<PhysicsEntity> PE_list;
  PhysicsEngine PE;
  ArrayList<Player> players;
  Tank currentTank;
  phase state;
  Projectile activeProjectile;
  int currentPlayer;
  int turnTimer;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);
		Rectangle worldBounds = new Rectangle(0, 0, container.getWidth()*2, container.getHeight()*2);
		Rectangle screenBounds = new Rectangle(0, 0, container.getWidth(), container.getHeight());//new Rectangle(0, 0, container.getScreenWidth(), container.getScreenHeight());
		world = new World(worldBounds);
		camera = new DebugCamera(screenBounds, worldBounds);
		System.out.println("world size: " + worldBounds + ", screen size: " + screenBounds);
		world.loadLevel("YAY");
	}
	
	@Override
  public void enter(GameContainer container, StateBasedGame game)
    throws SlickException {
    
    PE_list = new ArrayList<PhysicsEntity>();
    
    PE_list.add(new Projectile(20, 300, new Vector(2f, -2f)));
    
    PE = new PhysicsEngine(PE_list);

    //setup players
    players = new ArrayList<Player>();
    players.add(new Player(Color.blue));
    players.add(new Player(Color.green));
    players.get(0).addTank(50, 400);
    players.get(0).addTank(500, 400);
    players.get(1).addTank(200, 400);
    players.get(1).addTank(800, 400);
    state = phase.MOVEFIRE;
    currentPlayer = 0;
    currentTank = players.get(currentPlayer).getNextTank();
    turnTimer = TURNLENGTH;
  }

	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		Tanx bg = (Tanx) game;
		
		g.pushTransform();
		camera.transformContext(g);
		// Render anything that should be affected by the camera location.

		world.renderTerrain(g);
		PE_list.forEach((e)->e.render(g));
		players.forEach((t) ->t.render(g));

		//placeholder, should put an arrow sprite pointing to currently active tank
    g.drawString("Active", currentTank.getX() - 20, currentTank.getY() + 30);
		
		g.popTransform();
		// Render anything that shouldn't be transformed below here.
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		Input input = container.getInput();

		if (state == phase.MOVEFIRE){
		  turnTimer -= delta;
		  if (turnTimer <= 0){
		    turnTimer = TURNLENGTH;
		    currentTank = getNextTank(players);
      }
      if (input.isKeyDown(Input.KEY_E)){
        currentTank.rotate(Direction.RIGHT, delta);
      } else if (input.isKeyDown(Input.KEY_Q)){
        currentTank.rotate(Direction.LEFT, delta);
      }
      if (input.isKeyPressed(Input.KEY_SPACE)){
        activeProjectile = currentTank.fire(1);
        PE.addPhysicsEntity(activeProjectile);
        state = phase.FIRING;
      }
    } else if (state == phase.FIRING){
		  if (activeProjectile.isDead){
		    state = phase.MOVEFIRE;
		    currentTank = getNextTank(players);
      }
    }

    
    PE.update(delta);
		controlCamera(delta, input);
	}

  private Tank getNextTank(ArrayList<Player> pList) {
	  currentPlayer++;
	  if (pList.isEmpty()){
	    System.out.println("getNextTankERROR: No players in playerlist");
    } if (currentPlayer >= pList.size()){
	    currentPlayer = 0;
    }
	  return pList.get(currentPlayer).getNextTank();
  }

  private void controlCamera(int delta, Input input) {
    if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
      camera.setZoom(camera.getZoom() + 0.25f);
    }
    if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
      camera.setZoom(camera.getZoom() - 0.25f);
    }
    if (input.isKeyPressed(Input.KEY_O)) {
      camera.toggleDebug();
    }
    if (input.isKeyDown(Input.KEY_LEFT)) {
      camera.move(new Vector(-delta/3, 0));
    }
    if (input.isKeyDown(Input.KEY_RIGHT)) {
      camera.move(new Vector(delta/3, 0));
    }
    if (input.isKeyDown(Input.KEY_UP)) {
      camera.move(new Vector(0, -delta/3));
    }
    if (input.isKeyDown(Input.KEY_DOWN)) {
      camera.move(new Vector(0, delta/3));
    }
	}

	@Override
	public int getID() {
		return Tanx.PLAYINGSTATE;
	}
}
