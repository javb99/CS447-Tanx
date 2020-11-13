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
  ArrayList<PhysicsEntity> PE_list;
  PhysicsEngine PE;
  Tank t;
  
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
		world = new World(new Rectangle(0, 0, container.getWidth(), container.getHeight()));
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

		world.renderTerrain(g);
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
	}

	@Override
	public int getID() {
		return Tanx.PLAYINGSTATE;
	}
}
