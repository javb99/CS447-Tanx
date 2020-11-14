import java.util.ArrayList;

import org.newdawn.slick.Color;
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
  Camera camera;
  ArrayList<PhysicsEntity> PE_list;
  PhysicsEngine PE;
  Tank t;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
		Rectangle worldBounds = new Rectangle(0, 0, container.getWidth()*2, container.getHeight()*2);
		Rectangle screenBounds = new Rectangle(0, 0, container.getWidth(), container.getHeight());//new Rectangle(0, 0, container.getScreenWidth(), container.getScreenHeight());
		world = new World(worldBounds);
		camera = new Camera(screenBounds, worldBounds);
		System.out.println("world size: " + worldBounds + ", screen size: " + screenBounds);
		world.loadLevel("YAY");
	}
	
	@Override
  public void enter(GameContainer container, StateBasedGame game)
    throws SlickException {
    
    PE_list = new ArrayList<PhysicsEntity>();
    
    PE_list.add(new Projectile(20, 300, new Vector(2f, -2f)));
    
    PE = new PhysicsEngine(PE_list);
    t = new Tank(50, 400);
  }

	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		Tanx bg = (Tanx) game;
		
		System.out.println("render: " + camera.toString());
		g.pushTransform();
		camera.transformContext(g);
		// Render anything that should be affected by the camera location.

		world.renderTerrain(g);
		if (worldMouse != null) {
  		Vector center = camera.getWorldLocation();
  		g.setColor(Color.red);
  		g.setLineWidth(3);
  		g.drawLine(center.getX(), center.getY(), worldMouse.getX(), worldMouse.getY());
  		System.out.println("RED(w): " + center +" - " + worldMouse);
		}
		g.popTransform();
		// Render anything that shouldn't be transformed below here.
		
		if (worldMouse != null) {
      Vector center = camera.screenLocationForWorldLocation(camera.getWorldLocation());
      Vector screenMouse = camera.screenLocationForWorldLocation(worldMouse);
      g.setColor(Color.blue);
      g.setLineWidth(1);
      g.drawLine(center.getX(), center.getY(), screenMouse.getX(), screenMouse.getY());
      System.out.println("BLUE(s): " + center +" - " + screenMouse);
    }
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
    
    PE.update(delta);
		controlCamera(delta, input);
	}
	
	private void controlCamera(int delta, Input input) {
	}

	@Override
	public int getID() {
		return Tanx.PLAYINGSTATE;
	}
}
