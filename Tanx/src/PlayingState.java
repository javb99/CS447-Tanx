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

enum phase {MOVEFIRE, FIRING, TURNCHANGE};

public class PlayingState extends BasicGameState {
  static private int TURNLENGTH = 11*1000;
  static private int FIRING_TIMEOUT = 5*1000;
  static private int SHOTRESOLVE_TIMEOUT = 2*1000;
	World world;
	DebugCamera camera;
  ArrayList<PhysicsEntity> PE_list;
  PhysicsEngine PE;
  ArrayList<Player> players;
  phase state;
  Projectile activeProjectile;
  int pIndex;
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

    players = new ArrayList<Player>();


    PlayerConfigurator PC = new PlayerConfigurator(container.getWidth()*2, 4, 2);
    players = PC.config();
    

    PE_list.add(new AmmoPowerup(50, 200, Cannon.BIG_CANNON, 1));


    for (Player p: players){
      for (Tank t: p.getTanks()){
        PE_list.add(t);
      }
    }
    pIndex = 0;
    changePlayer();

    PE = new PhysicsEngine(PE_list, world);
    
    // Example use case. Probably not complete.
    PE.registerCollisionHandler(Tank.class, Terrain.class, (tank, terrain, c) -> {
      if (tank.getY() < terrain.getY()) {
        tank.setOnGround(true);
      }
    });

    PE.registerCollisionHandler(Powerup.class, Tank.class, (powerup, tank, c) -> {
      powerup.usePowerup(tank);
    });

    PE.registerCollisionHandler(Projectile.class, PhysicsEntity.class, (projectile, obstacle, c) -> {
      if (obstacle instanceof Projectile) { return; } // Don't explode on other projectiles.
      if (projectile == activeProjectile && state == phase.FIRING) { turnTimer = SHOTRESOLVE_TIMEOUT; }
      projectile.explode();
    });
    
    camera.toggleDebug();
  }

	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		Tanx bg = (Tanx) game;
		
		g.pushTransform();
		camera.transformContext(g);
		// Render anything that should be affected by the camera location.

    world.terrain.render(g);
		PE_list.forEach((e)->e.render(g));
		players.forEach((t) ->t.render(g));

		//placeholder, should put an arrow sprite pointing to currently active tank
    if (state == phase.MOVEFIRE){
      Tank currentTank = players.get(pIndex).getTank();
      g.drawString("Active", currentTank.getX() - 20, currentTank.getY() + 30);
      g.drawString(Integer.toString(turnTimer/1000), currentTank.getX() - 40, currentTank.getY() + 30);
      g.drawString(Integer.toString(players.get(pIndex).getAmmo()), currentTank.getX() + 40, currentTank.getY() + 30);
    }
		
		camera.renderDebugOverlay(g);
		
		g.popTransform();
		// Render anything that shouldn't be transformed below here.
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		Input input = container.getInput();

    turnTimer -= delta;
		if (state == phase.MOVEFIRE){ ;
		  Tank currentTank = players.get(pIndex).getTank();
		  if (turnTimer <= 0){
		    changePlayer();
      }

      if (input.isKeyDown(Input.KEY_E)){
        currentTank.rotate(Direction.RIGHT, delta);
      } else if (input.isKeyDown(Input.KEY_Q)){
        currentTank.rotate(Direction.LEFT, delta);
      }
      if (input.isKeyPressed(Input.KEY_C)){
        players.get(pIndex).nextWeapon();
      }
      if (input.isKeyPressed(Input.KEY_Z)){
        players.get(pIndex).prevWeapon();
      }
      if (input.isKeyPressed(Input.KEY_SPACE)){
        activeProjectile = currentTank.fire(1);
        PE.addPhysicsEntity(activeProjectile);
        camera.trackObject(activeProjectile);
        state = phase.FIRING;
        turnTimer = FIRING_TIMEOUT;
      }
    } else if(state == phase.FIRING) {
      if (turnTimer <= 0) { camera.stopTracking(); changePlayer(); }
    } if (state == phase.TURNCHANGE) {
		  //For safety, timeout if there are issues-soft bug
        if(camera.getState() == camState.IDLE) {
          state = phase.MOVEFIRE;
        }
    }

		for(Player p: players){p.update(delta);}
    PE.update(delta);
		controlCamera(delta, input);
		world.update(delta, PE, players);
	}

  private void changePlayer() {
    activeProjectile = null;
    state = phase.TURNCHANGE;
    turnTimer = TURNLENGTH;
    pIndex ++;
    if (pIndex >= players.size()){pIndex = 0;}
    Player currentPlayer = players.get(pIndex);
    currentPlayer.getNextTank();
    currentPlayer.checkWeapon();
    camera.moveTo(currentPlayer.getTank().getPosition());
  }

  private void controlCamera(int delta, Input input) {
	  if (camera.getState() == camState.IDLE){
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
    camera.update(delta);
	}

	@Override
	public int getID() {
		return Tanx.PLAYINGSTATE;
	}
}
