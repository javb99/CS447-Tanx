import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import jig.Entity;
import jig.Vector;

public class PlayingState extends BasicGameState {
	World world;
	DebugCamera camera;
  ArrayList<PhysicsEntity> PE_list;
  PhysicsEngine PE;
  Tank t;

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
    
    PE = new PhysicsEngine(PE_list, world);
    t = new Tank(world.geometry.tilesArea.getCenterX(), world.geometry.tilesArea.getCenterY());
    PE.addPhysicsEntity(t);
    
    // Example use case. Probably not complete.
    PE.registerCollisionHandler(Tank.class, Terrain.class, (tank, terrain, c) -> {
      if (tank.getY() < terrain.getY()) {
        tank.setOnGround(true);
      }
    });
    
    PE.registerCollisionHandler(Projectile.class, PhysicsEntity.class, (projectile, obstacle, c) -> {
      if (obstacle instanceof Projectile) { return; } // Don't explode on other projectiles.
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
		t.render(g);
		
		g.popTransform();
		// Render anything that shouldn't be transformed below here.
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		Input input = container.getInput();
		
		if (input.isKeyDown(Input.KEY_E)){
      t.rotate(Direction.RIGHT, delta);
    } else if (input.isKeyDown(Input.KEY_Q)){
      t.rotate(Direction.LEFT, delta);
    }
    if (input.isKeyPressed(Input.KEY_SPACE)){
      PE.addPhysicsEntity(t.fire(1));
    }
    
    t.update(delta);
    PE.update(delta);
		controlCamera(delta, input);
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
