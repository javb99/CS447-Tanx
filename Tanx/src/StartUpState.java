import jig.ResourceManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;

public class StartUpState extends BasicGameState {

	private ArrayList<MenuOption> main;
	private ArrayList<MenuOption> credits;
	private ArrayList<MenuOption> setup;
	private int selectedOption;
	
	enum Menu{
		MAIN,
		CREDITS,
		SETUP
	}
	
	private Menu currentMenu;
	
	private final static String MAIN_PLAY = "Play";
	private final static String MAIN_CREDITS = "Credits";
	private final static String CREDITS_RETURN = "Return to Main Menu";
	private final static String SETUP_START = "Begin Game";
	private final static String SETUP_PLAYERS = "Players:";
	private final static String SETUP_TANKS = "Tanks per player:";
	private final static String SETUP_WORLD_SIZE = "World Size:";
	
	
  @Override
  public void init(GameContainer container, StateBasedGame game)
      throws SlickException {
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) {
    container.setSoundOn(true);
    
    Input input = container.getInput();
    clearInputBuffer(input);
    
    selectedOption = 0;
    currentMenu = Menu.MAIN;
    
    main = new ArrayList<MenuOption>();
    main.add(new MenuOption(container.getWidth()/2, container.getHeight()/2 - 80, MAIN_PLAY));
    main.add(new MenuOption(container.getWidth()/2, container.getHeight()/2 - 60, MAIN_CREDITS));
    
    credits = new ArrayList<MenuOption>();
    credits.add(new MenuOption(container.getWidth()/2, container.getHeight() - 100, CREDITS_RETURN));
    
    setup = new ArrayList<MenuOption>();
    
    ArrayList<String> list = new ArrayList<String>();
    list.add("1");
    list.add("2");
    list.add("3");
    list.add("4");
    setup.add(new MultiValueOption(container.getWidth()/2, container.getHeight()/2, SETUP_TANKS, list));
    list.remove("1");
    setup.add(new MultiValueOption(container.getWidth()/2, container.getHeight()/2+20, SETUP_PLAYERS, list));
    list = new ArrayList<String>();
    list.add("SMALL");
    list.add("MEDIUM");
    list.add("LARGE");
    setup.add(new MultiValueOption(container.getWidth()/2, container.getHeight()/2+40, SETUP_WORLD_SIZE, list));
    setup.add(new MenuOption(container.getWidth()/2, container.getHeight()/2+60, SETUP_START));
  }


  @Override
  public void render(GameContainer container, StateBasedGame game,
                     Graphics g) throws SlickException {
    Tanx bg = (Tanx) game;
    
    ArrayList<MenuOption> options;
    
    switch(currentMenu) {
    case MAIN:
    	options = main;
    	Image logo = ResourceManager.getImage(Tanx.SPLASH_LOGO).getScaledCopy(.5f);
    	
    	g.drawImage(logo, container.getWidth()/2 - logo.getWidth()/2, container.getHeight()/2 - 100);
    	break;
    case CREDITS:
    	options = credits;
    	break;
    case SETUP:
    	options = setup;
    	break;
    default:
    	options = main;
    }
    
    for(int i=0; i<options.size(); i++) {
    	options.get(i).render(g, i == selectedOption ? true : false);
    }

  }

  @Override
  public void update(GameContainer container, StateBasedGame game,
                     int delta) throws SlickException {
	  
	Input input = container.getInput();
	  
    
    Tanx bg = (Tanx) game;
    ArrayList<MenuOption> options;
    
    switch(currentMenu) {
    case MAIN:
    	options = main;
    	break;
    case CREDITS:
    	options = credits;
    	break;
    case SETUP:
    	options = setup;
    	break;
    default:
    	options = main;
    }
    
    if(input.isKeyPressed(Input.KEY_UP) || input.isKeyPressed(Input.KEY_W)) {
    	selectedOption--;
    	if(selectedOption < 0) selectedOption = options.size()-1;
    }
    if(input.isKeyPressed(Input.KEY_DOWN) || input.isKeyPressed(Input.KEY_S)) {
    	selectedOption++;
    	if(selectedOption > options.size() - 1) selectedOption = 0;
    }
    if(input.isKeyPressed(Input.KEY_ENTER) || input.isKeyPressed(Input.KEY_SPACE)) {
    	handleOption(currentMenu, selectedOption, bg);
    }
    if(input.isKeyPressed(Input.KEY_A) || input.isKeyPressed(Input.KEY_LEFT)) {
    	if(currentMenu == Menu.SETUP) {
    		if(setup.get(selectedOption).getClass().toString().equals("class MultiValueOption")) {
    			((MultiValueOption)setup.get(selectedOption)).prev();
    		}
    	}
    }
    if(input.isKeyPressed(Input.KEY_D) || input.isKeyPressed(Input.KEY_RIGHT)) {
    	if(currentMenu == Menu.SETUP) {
    		if(setup.get(selectedOption).getClass().toString().equals("class MultiValueOption")) {
    			((MultiValueOption)setup.get(selectedOption)).next();
    		}
    	}
    }
  }
  
  void handleOption(Menu menu, int option, Tanx game){
	  switch(menu) {
	  case MAIN:
		  switch(main.get(option).getLabel()) {
		  case MAIN_PLAY:
			  currentMenu = Menu.SETUP;
			  selectedOption = 0;
			  break;
		  case MAIN_CREDITS:
			  currentMenu = Menu.CREDITS;
			  selectedOption = 0;
			  break;
		  }
		  break;
	  case CREDITS:
		  switch(credits.get(option).getLabel()) {
		  case CREDITS_RETURN:
			  currentMenu = Menu.MAIN;
			  selectedOption = 0;
			  break;
		  }
		  break;
	  case SETUP:
		  switch(setup.get(option).getLabel()) {
		  case SETUP_START:
			  game.enterState(Tanx.PLAYINGSTATE);
			  break;
		  default:
			  break;
		  }
		  break;
	  }
  }

  @Override
  public int getID() {
    return Tanx.STARTUPSTATE;
  }
  
  public void clearInputBuffer(Input in) {
	  in.isKeyPressed(Input.KEY_W);
	  in.isKeyPressed(Input.KEY_A);
	  in.isKeyPressed(Input.KEY_S);
	  in.isKeyPressed(Input.KEY_D);
	  in.isKeyPressed(Input.KEY_UP);
	  in.isKeyPressed(Input.KEY_LEFT);
	  in.isKeyPressed(Input.KEY_DOWN);
	  in.isKeyPressed(Input.KEY_RIGHT);
	  in.isKeyPressed(Input.KEY_SPACE);
	  in.isKeyPressed(Input.KEY_ENTER);
  }


}
