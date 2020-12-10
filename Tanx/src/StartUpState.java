import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.Iterator;

public class StartUpState extends BasicGameState {

  @Override
  public void init(GameContainer container, StateBasedGame game)
      throws SlickException {
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) {

    container.setSoundOn(true);
    container.setSoundVolume(.5f);
  }


  @Override
  public void render(GameContainer container, StateBasedGame game,
                     Graphics g) throws SlickException {
    Tanx bg = (Tanx) game;

  }

  @Override
  public void update(GameContainer container, StateBasedGame game,
                     int delta) throws SlickException {

    Input input = container.getInput();
    Tanx bg = (Tanx) game;
    bg.enterState(Tanx.PLAYINGSTATE);

  }

  @Override
  public int getID() {
    return Tanx.STARTUPSTATE;
  }


}
