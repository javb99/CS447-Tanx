import jig.ResourceManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;
import java.util.Collections;

public class StartUpState extends BasicGameState {
	
	private ArrayList<MenuOption> main;
	private ArrayList<MenuOption> setup;
	private int selectedOption;
	
	enum Menu{
		MAIN,
		SETUP
	}
	
	private Menu currentMenu;
	
	private final static String MAIN_PLAY = "Play";
	private final static String SETUP_START = "Begin Game";
	private final static String SETUP_PLAYERS = "Players:";
	private final static String SETUP_TANKS = "Tanks per player:";
	private final static String SETUP_WORLD_SIZE = "World Size:";
	private final static String SETUP_RETURN = "Return to Main Menu";
	private final static String SETUP_INVALID = "SMALL MAPS CAN ONLY HANDLE SIX OR LESS TANKS TOTAL";
	private final static String SETUP_TURN_TIME = "Turn Length (seconds):";
	
	private MultiValueOption players;
	private MultiValueOption tanks;
	private MultiValueOption worldSize;
	private MultiValueOption turnLength;
	
	private boolean loading;
	private boolean validSettings;
	
	
  @Override
  public void init(GameContainer container, StateBasedGame game)
      throws SlickException {
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) {

    Input input = container.getInput();
    clearInputBuffer(input);
    
    selectedOption = 0;
    currentMenu = Menu.MAIN;
    
    main = new ArrayList<MenuOption>();
    main.add(new MenuOption(container.getWidth()/2 - 10, container.getHeight()/2 - 80, MAIN_PLAY));
    
    setup = new ArrayList<MenuOption>();
    
    ArrayList<String> list = new ArrayList<String>();
    list.add("1");
    list.add("2");
    list.add("3");
    list.add("4");
    tanks = new MultiValueOption(container.getWidth()/2, container.getHeight()/2-35, SETUP_TANKS, list);
    setup.add(tanks);
    
    list = new ArrayList<String>();
    list.add("2");
    list.add("3");
    list.add("4");
    players = new MultiValueOption(container.getWidth()/2, container.getHeight()/2-15, SETUP_PLAYERS, list);
    setup.add(players);
    
    list = new ArrayList<String>();
    list.add("SMALL");
    list.add("LARGE");
    worldSize = new MultiValueOption(container.getWidth()/2, container.getHeight()/2+5, SETUP_WORLD_SIZE, list);
    setup.add(worldSize);
    
    list = new ArrayList<String>();
    list.add("10");
    list.add("20");
    list.add("30");
    list.add("40");
    list.add("50");
    list.add("60");
    turnLength = new MultiValueOption(container.getWidth()/2, container.getHeight()/2+25, SETUP_TURN_TIME, list);
    setup.add(turnLength);
    setup.add(new MenuOption(container.getWidth()/2, container.getHeight()/2+45, SETUP_START));
    setup.add(new MenuOption(container.getWidth()/2, container.getHeight()/2+75, SETUP_RETURN));
    
    loading = false;


    container.setSoundOn(true);
    container.setSoundVolume(.5f);
    ResourceManager.getSound(Tanx.MENU_MUSIC).loop(1f, .3f);

  }


  @Override
  public void render(GameContainer container, StateBasedGame game,
                     Graphics g) throws SlickException {
    Tanx bg = (Tanx) game;
    
    ArrayList<MenuOption> options;
    
    g.drawImage(ResourceManager.getImage(Tanx.MENU_BACKGROUND), 0, 0);
    
    
    if(loading) {
    	g.setColor(Color.black);
    	g.drawString("LOADING", container.getWidth()/2, container.getHeight()/2+25);
    	g.setColor(Color.white);
    	return;
    }
    
    switch(currentMenu) {
    case MAIN:
    	options = main;
    	
    	Image logo = ResourceManager.getImage(Tanx.SPLASH_LOGO).getScaledCopy(.5f);
    	
    	g.drawImage(logo, container.getWidth()/2 - logo.getWidth()/2, container.getHeight()/2 - 100);
    	break;
    case SETUP:
    	if(!validSettings) {
    		g.setColor(Color.red);
    		g.drawString(SETUP_INVALID, container.getWidth()/2 - g.getFont().getWidth(SETUP_INVALID)/2, container.getHeight()/2 - 120);
    	}
    	options = setup;
    	break;
    default:
    	options = main;
    }
    
    g.setColor(Color.black);
    
    for(int i=0; i<options.size(); i++) {
    	if(!validSettings && options.get(i).getLabel().equals(SETUP_START)) {
    		g.setColor(Color.red);
    	}
    	options.get(i).render(g, i == selectedOption ? true : false);
    	g.setColor(Color.black);
    }
    
    g.setColor(Color.white);

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
    	handleOption(currentMenu, selectedOption, bg, container);
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
    
    
    validSettings = true;
    if(Integer.parseInt(players.getSelection()) * Integer.parseInt(tanks.getSelection()) > 6){
    	if(worldSize.getSelection().equals("SMALL")) {
    		validSettings = false;
    	}
    }
  }
  
  void handleOption(Menu menu, int option, Tanx game, GameContainer container){
	  switch(menu) {
	  case MAIN:
		  switch(main.get(option).getLabel()) {
		  case MAIN_PLAY:
			  currentMenu = Menu.SETUP;
			  selectedOption = 0;
			  break;
		  }
		  break;
	  case SETUP:
		  switch(setup.get(option).getLabel()) {
		  case SETUP_START:
			  if(validSettings) {
				  transitionToPlayingState(container, game);
			  }
			  break;
		  case SETUP_RETURN:
			  currentMenu = Menu.MAIN;
			  selectedOption = 0;
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
  
  private void transitionToPlayingState(GameContainer container, Tanx game) {
	  loading = true;
	  int width = 0;
	  int height = 0;
	  switch(worldSize.getSelection()) {
	  case "LARGE":
		  width = 4*container.getWidth();
		  height = 2*container.getHeight();
		  break;
	  case "SMALL":
	  default:
		  width = 2*container.getWidth();
		  height = 2*container.getHeight();
		  break;
	  }
	  Rectangle bounds = new Rectangle(0, 0, width, height);
	  PlayerConfigurator PC = new PlayerConfigurator(width, Integer.parseInt(players.getSelection()), Integer.parseInt(tanks.getSelection()));
	  ((PlayingState)game.getState(Tanx.PLAYINGSTATE)).setPlayerConfig(PC);
	  ((PlayingState)game.getState(Tanx.PLAYINGSTATE)).setWorldBounds(bounds);
	  PlayingState.TURNLENGTH = Integer.parseInt(turnLength.getSelection())*1000;
	  System.out.println(PlayingState.TURNLENGTH);
	  if(ResourceManager.getSound(Tanx.MENU_MUSIC).playing()) {
			ResourceManager.getSound(Tanx.MENU_MUSIC).stop();
	  }
	  game.enterState(Tanx.PLAYINGSTATE);
  }
  
  
  private void clearInputBuffer(Input in) {
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
