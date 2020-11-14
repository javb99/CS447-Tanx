import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import jig.Vector;

public class PlayingState extends BasicGameState {
	
	Projectile p;
	Tank t;
	
  @Override
  public void init(GameContainer container, StateBasedGame game)
      throws SlickException {
  }
  
  @Override
  public void enter(GameContainer container, StateBasedGame game)
  	throws SlickException {
	  
	  p = new Projectile(30, 400, new Vector(.2f, -.2f));
	  t = new Tank(50, 400);
  }

  @Override
  public void render(GameContainer container, StateBasedGame game,
                     Graphics g) throws SlickException {
    Tanx bg = (Tanx) game;
    
    p.render(g);
    t.render(g);
    t.renderCannon(g);
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
      p = t.fire(1);
    }
    
    p.update(delta);
    
  }

  @Override
  public int getID() {
    return Tanx.PLAYINGSTATE;
  }
}
