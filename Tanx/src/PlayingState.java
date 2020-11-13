import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import jig.Vector;

public class PlayingState extends BasicGameState {
	
	Projectile p;
	
  @Override
  public void init(GameContainer container, StateBasedGame game)
      throws SlickException {
  }
  
  @Override
  public void enter(GameContainer container, StateBasedGame game)
  	throws SlickException {
	  
	  p = new Projectile(30, 400, new Vector(.2f, -.2f));
  }

  @Override
  public void render(GameContainer container, StateBasedGame game,
                     Graphics g) throws SlickException {
    Tanx bg = (Tanx) game;
    
    p.render(g);
  }

  @Override
  public void update(GameContainer container, StateBasedGame game,
                     int delta) throws SlickException {
    Input input = container.getInput();
    
    p.update(delta);
    
  }

  @Override
  public int getID() {
    return Tanx.PLAYINGSTATE;
  }
}
