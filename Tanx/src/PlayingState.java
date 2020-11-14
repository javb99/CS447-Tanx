import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import jig.Vector;

public class PlayingState extends BasicGameState {
	
	ArrayList<PhysicsEntity> PE_list;
	PhysicsEngine PE;
	
  @Override
  public void init(GameContainer container, StateBasedGame game)
      throws SlickException {
  }
  
  @Override
  public void enter(GameContainer container, StateBasedGame game)
  	throws SlickException {
	  
	  PE_list = new ArrayList<PhysicsEntity>();
	  
	  PE_list.add(new Projectile(20, 300, new Vector(2f, -2f)));
	  
	  PE = new PhysicsEngine(PE_list);
  }

  @Override
  public void render(GameContainer container, StateBasedGame game,
                     Graphics g) throws SlickException {
    Tanx bg = (Tanx) game;
    
    PE_list.forEach((n) -> n.render(g));
  }

  @Override
  public void update(GameContainer container, StateBasedGame game,
                     int delta) throws SlickException {
    Input input = container.getInput();
    
    PE.update(delta);
    
    if(input.isKeyPressed(Input.KEY_SPACE)) {
    	PE.addPhysicsEntity(new Projectile(20, 600, new Vector(2.5f, -5f)));
    }
    
  }

  @Override
  public int getID() {
    return Tanx.PLAYINGSTATE;
  }
}
