import java.util.ArrayList;
import java.util.Iterator;

import java.util.ArrayList;
import java.util.Iterator;

import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

  /**
   * A Simple Game of Tanx. (Skeleton leveraged from Bounce game.)
   *
   * The game has three states: StartUp, Playing, and GameOver, the game
   * progresses through these states based on the user's input and the events that
   * occur. Each state is modestly different in terms of what is displayed and
   * what input is accepted.
   *
   *
   *
   * Skeleton Code Copied from Bounce by wallaces
   * @author Matthew Scofield
   *
   */
  public class Tanx extends StateBasedGame {

    public static final int STARTUPSTATE = 0;
    public static final int PLAYINGSTATE = 1;
    public static final int GAMEOVERSTATE = 2;

    //ex: public static final String BALL_BALLIMG_RSC = "bounce/resource/ball.png";

    public final int ScreenWidth;
    public final int ScreenHeight;

    /**
     * Create the Keep Away frame, saving the width and height for later use.
     *
     * @param title
     *            the window's title
     * @param width
     *            the window's width
     * @param height
     *            the window's height
     */
    public Tanx(String title, int width, int height) {
      super(title);
      ScreenHeight = height;
      ScreenWidth = width;
      Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);

    }


    @Override
    public void initStatesList(GameContainer container) throws SlickException {
     // addState(new StartUpState());
     // addState(new GameOverState());
      addState(new PlayingState());

      // the sound resource takes a particularly long time to load,
      // we preload it here to (1) reduce latency when we first play it
      // and (2) because loading it will load the audio libraries and
      // unless that is done now, we can't *disable* sound as we
      // attempt to do in the startUp() method.
      //ex: ResourceManager.loadSound(BANG_EXPLOSIONSND_RSC);

      // preload all the resources to avoid warnings & minimize latency...
      //ex: ResourceManager.loadImage(BALL_BALLIMG_RSC);

    }

    public static void main(String[] args) {
      AppGameContainer app;
      try {
        app = new AppGameContainer(new Tanx("Tanx", 800, 600));
        app.setDisplayMode(800, 600, false);
        app.setVSync(true);
        app.start();
      } catch (SlickException e) {
        e.printStackTrace();
      }

    }
}
