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
  Terrain trn;

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
		
		
		Terrain.TerrainType mask[][] = new Terrain.TerrainType[container.getWidth()*2][container.getHeight()*2];
		for(int x = 0; x < mask.length; x++) {
			for(int y = 0; y < mask[x].length; y++) {
				mask[x][y] = Terrain.TerrainType.NORMAL;
			}
		}
		
		trn = new Terrain(container.getWidth()*2, container.getHeight()*2, mask);
		trn.setTerrainInCircle(new Vector(200, 200), 500, Terrain.TerrainType.OPEN);
		trn.setTerrainInLine(new Vector(200, 200), new Vector(700, 200), Terrain.TerrainType.NORMAL);
	}
	
	@Override
  public void enter(GameContainer container, StateBasedGame game)
    throws SlickException {
    
    PE_list = new ArrayList<PhysicsEntity>();
    
    PE_list.add(new Projectile(20, 300, new Vector(2f, -2f)));
    
    PE = new PhysicsEngine(PE_list, trn);
    t = new Tank(50, 400);
  }

	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		Tanx bg = (Tanx) game;
		
		g.pushTransform();
		camera.transformContext(g);
		// Render anything that should be affected by the camera location.

		trn.render(g);
		world.renderTerrain(g);
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
