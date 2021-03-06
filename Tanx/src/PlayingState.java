import java.util.ArrayList;
import org.newdawn.slick.GameContainer;
import jig.ResourceManager;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.geom.Circle;

import jig.Entity;
import jig.Vector;

enum phase {MOVEFIRE, FIRING, CHARGING, TURNCHANGE, GAMEOVER};

public class PlayingState extends BasicGameState {
	
  final int NO_WINNER_ID = -1;
  static public int TURNLENGTH;
  static public int INPUT_TIMER_CD = 100;
  static public int FIRING_TIMEOUT = 5*1000;
  static public int SHOTRESOLVE_TIMEOUT = 2*1000;
  static public int BOTTOM_UI_HEIGHT = 300;
  
  PlayerConfigurator PC;
  Rectangle worldBounds;
	
	World world;
	DebugCamera camera;
	Ui ui;
  ArrayList<PhysicsEntity> PE_list;
  PhysicsEngine PE;
  ArrayList<Player> players;
  ProjectileSystem projectileSystem;
  ExplosionSystem explosionSystem;
  FireSystem fireSystem;
  phase state;
  Projectile activeProjectile;
  int pIndex;
  int turnTimer;
  ActiveTankArrow tankPointer;
  boolean toggleCheats;
  int cleanInputTimer;

  	public void setPlayerConfig(PlayerConfigurator pc) {
  		PC = pc;
  	}
  	
  	public void setWorldBounds(Rectangle wb) {
  		worldBounds = wb;
  	}
  
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
    Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);
	}
	
	@Override
  public void enter(GameContainer container, StateBasedGame game)
    throws SlickException {
	
	ResourceManager.getSound(Tanx.BATTLE_MUSIC).loop(1, .1f);
		
    Rectangle screenBounds = new Rectangle(0, 0, container.getWidth(), container.getHeight() - BOTTOM_UI_HEIGHT/2);//new Rectangle(0, 0, container.getScreenWidth(), container.getScreenHeight());
    Rectangle bottomUiBounds = new Rectangle(0, 0, screenBounds.getWidth(), BOTTOM_UI_HEIGHT);
    Vector bottomUiPosition = new Vector(screenBounds.getWidth()/4, BOTTOM_UI_HEIGHT);
    ui = new Ui(bottomUiBounds, bottomUiPosition);
    world = new World(worldBounds);
    camera = new DebugCamera(screenBounds, worldBounds);
    System.out.println("world size: " + worldBounds + ", screen size: " + screenBounds);
    world.loadLevel("YAY");
    explosionSystem = new ExplosionSystem();
    projectileSystem = new ProjectileSystem();
    fireSystem = new FireSystem(world.terrain);

    PE_list = new ArrayList<PhysicsEntity>();

    players = new ArrayList<Player>();

    players = PC.config();

    for (Player p: players){
      for (Tank t: p.getTanks()){
        PE_list.add(t);
      }
    }
    tankPointer = new ActiveTankArrow(0, 0);
    pIndex = 0;
    changePlayer();

    PE = new PhysicsEngine(PE_list, world);
    
    PE.setCollisionPredicate((a, b) -> {
      boolean isAProjectile = a instanceof Projectile;
      boolean isBProjectile = b instanceof Projectile;
      if (isAProjectile && isBProjectile) return false;
      boolean isAFire = a instanceof GroundFire;
      boolean isBFire = b instanceof GroundFire;
      if (isAFire && isBFire) return false;
      if ((isAFire && isBProjectile) || (isAProjectile && isBFire)) {
        return false;
      }
      if (isAFire || isBFire) return true;
      if (isAProjectile || isBProjectile) return true;
      boolean isATank = a instanceof Tank;
      boolean isBTank = b instanceof Tank;
      if (isATank && isBTank) return false;
      if (isATank || isBTank) return true;
      return false;
    });
    
    // Example use case. Probably not complete.
    PE.registerCollisionHandler(Tank.class, Terrain.class, (tank, terrain, c) -> {
      if (tank.getY() < terrain.getY()) {
        tank.setOnGround(true);
      }
    });

    PE.registerCollisionHandler(Powerup.class, Tank.class, (powerup, tank, c) -> {
      powerup.usePowerup(tank);
    });

    PE.registerCollisionHandler(Tank.class, GroundFire.class, (tank, fire, c) -> {
      fire.applyFire(tank);
    });

    PE.registerCollisionHandler(Powerup.class, GroundFire.class, (powerup, fire, c) -> {
      fire.setDead(true);
      powerup.setDead(true);
      ResourceManager.getSound(Tanx.FIRE_DEBUFF_SND).play();
    });

    PE.registerCollisionHandler(Projectile.class, PhysicsEntity.class, (projectile, obstacle, c) -> {
      if (obstacle instanceof Projectile) { return; } // Don't explode on other projectiles.
      if (obstacle instanceof GroundFire) { return; } // Don't explode on GroundFire Entities
      if (projectile instanceof ClusterProjectile || projectile instanceof FireClusterProjectile ||
              projectile instanceof FireMiniBomb) {
        Vector location = projectile.getPosition();
        float blastRadius = 15;
        explosionSystem.addExplosion(location, blastRadius, Tanx.BANG_EXPLOSIONIMG_RSC, Tanx.BANG_EXPLOSIONSND_RSC);
      }
      if (projectile instanceof FireMiniBomb) {
        GroundFire newFire = new GroundFire(projectile.getX(), projectile.getY() + FireMiniBomb.Y_SPAWN_OFFSET);
        fireSystem.addFire(newFire);
        PE.addPhysicsEntity(newFire);
      }
      if (projectile.getTerrainInteraction() != Projectile.TerrainInteraction.BASIC) {return;}
      if (projectile == activeProjectile && state == phase.FIRING) { turnTimer = SHOTRESOLVE_TIMEOUT; }
      projectile.explode();
      int blastRadius = projectile.getExplosionRadius();
      int damage = projectile.getDamage();
      Vector location = projectile.getPosition();
      explosionSystem.addExplosion(location, (float)blastRadius, Tanx.BANG_EXPLOSIONIMG_RSC, Tanx.BANG_EXPLOSIONSND_RSC);
      world.terrain.setTerrainInCircle(location, blastRadius, Terrain.TerrainType.OPEN, true);
      
      PE.forEachEntityInCircle(location, (float)blastRadius, (e) -> {
        if (e instanceof Tank) {
          Tank tank = (Tank)e;
          tank.takeDamage(damage);
        }
      });
    });
    
    PE.registerCollisionHandler(MiniBomb.class, PhysicsEntity.class, (projectile, obstacle, c) -> {
        if (obstacle instanceof Projectile) { return; } // Don't explode on other projectiles.
        if (projectile == activeProjectile && state == phase.FIRING) { turnTimer = SHOTRESOLVE_TIMEOUT; }
          projectile.explode();
        int blastRadius = projectile.getExplosionRadius();
        int damage = projectile.getDamage();
        Vector location = projectile.getPosition();
        explosionSystem.addExplosion(location, (float)blastRadius, Tanx.BANG_EXPLOSIONIMG_RSC, Tanx.BANG_EXPLOSIONSND_RSC);
        
        //set the terrain bitmap but dont apply the change to the terrain image yet
        world.terrain.setTerrainInCircle(location, blastRadius, Terrain.TerrainType.OPEN, false);
        
        
        PE.forEachEntityInCircle(location, (float)blastRadius, (e) -> {
          if (e instanceof Tank) {
            Tank tank = (Tank)e;
            tank.takeDamage(damage);
          }
        });
        
        //remove this projectile from the projectile group
        projectile.getParent().getBombList().remove(projectile);
        
        //if there are no more projectiles in the group, we can now apply the mask
        if(projectile.getParent().getBombList().size() == 0) {
        	world.terrain.applyMask();
        }
      });
    

    PE.registerCollisionHandler(MountainMaker.class, PhysicsEntity.class, (mm, obstacle, c) -> {
        if (obstacle instanceof Projectile) { return; } // Don't explode on other projectiles.
        if (mm == activeProjectile && state == phase.FIRING) { turnTimer = SHOTRESOLVE_TIMEOUT; }
        mm.explode();
        int blastRadius = mm.getExplosionRadius();
        int damage = mm.getDamage();
        Vector location = mm.getPosition();
        
        explosionSystem.addExplosion(location, (float)(blastRadius*1.5), Tanx.BANG_MOUNTAINIMG_RSC, Tanx.BANG_MOUNTAINSND_RSC);
        world.terrain.changeTerrainInCircle(location, blastRadius, Terrain.TerrainType.OPEN, Terrain.TerrainType.NORMAL, false);
        
        ArrayList<Circle> holes = new ArrayList<Circle>();
        
        PE.forEachEntityInCircle(location, (float)blastRadius, (e) -> {
          if (e instanceof Tank) {
            Tank tank = (Tank)e;
            tank.takeDamage(damage);
          }
          if (!(e instanceof Projectile || e instanceof Terrain || e instanceof GroundFire)) {
        	  holes.add(new Circle(e.getX(), e.getY(), e.getCoarseGrainedRadius() + 30));
          }
          
          if (e instanceof GroundFire) {
        	  ((GroundFire) e).setDead(true);
        	  
          }
        });
        
        world.terrain.setTerrainInCircleList(holes, Terrain.TerrainType.OPEN);
      });
    
    PE.registerCollisionHandler(IceBomb.class, PhysicsEntity.class, (ib, obstacle, c) -> {
        if (obstacle instanceof Projectile) { return; } // Don't explode on other projectiles.
        if (ib == activeProjectile && state == phase.FIRING) { turnTimer = SHOTRESOLVE_TIMEOUT; }
        ib.explode();
        int blastRadius = ib.getExplosionRadius();
        int damage = ib.getDamage();
        Vector location = ib.getPosition();
        
        explosionSystem.addExplosion(location, (float)(blastRadius), Tanx.BANG_ICEIMG_RSC, Tanx.BANG_ICESND_RSC);
        world.terrain.changeTerrainInCircle(location, blastRadius, Terrain.TerrainType.NORMAL, Terrain.TerrainType.ICE, true);
        
        PE.forEachEntityInCircle(location, (float)blastRadius, (e) -> {
          if (e instanceof Tank) {
            Tank tank = (Tank)e;
            tank.takeDamage(damage);
          }
        });
      });
    

    //camera.toggleDebug();

  }

	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
    Tanx bg = (Tanx) game;

    g.pushTransform();
    camera.transformContext(g);
    // Render anything that should be affected by the camera location.

    g.drawImage(ResourceManager.getImage(Tanx.BATTLE_BACKGROUND).getScaledCopy((int)worldBounds.getWidth(), (int)worldBounds.getHeight()*2), 0, -3*worldBounds.getHeight()/4);
    world.terrain.render(g);
    PE_list.forEach((e) -> e.render(g));
    players.forEach((p) -> p.render(g));
    explosionSystem.render(g);
    projectileSystem.render(g);
    fireSystem.render(g);

    //placeholder, should put an arrow sprite pointing to currently active tank
    if (state == phase.MOVEFIRE || state == phase.TURNCHANGE || state == phase.CHARGING) {
      tankPointer.render(g);
    }

    camera.renderDebugOverlay(g);

    g.popTransform();
    // Render anything that shouldn't be transformed below here.
    ui.render(g);
    if (toggleCheats) {
      Player current = players.get(pIndex);
      float yOffset = 20;
      g.drawString("CHEATS ON", 0, yOffset);
      if (current.isInfFuel()) {
        yOffset += 20;
        g.drawString("Infinite Fuel On!", 0, yOffset);
      }
      if (current.isInfHealth()) {
        yOffset += 20;
        g.drawString("Current Tank has Infinite Health!", 0, yOffset);
      }
    }
    if (state == phase.GAMEOVER) {
      renderGameOver(g, bg);
    } else if (state == phase.TURNCHANGE) {
      renderTurnChange(g, bg);
    }
  }
	

  private void renderTurnChange(Graphics g, Tanx bg) {
    final float INSTRUCT_X = bg.ScreenWidth/2 - 200;
    final float INSTRUCT_Y = 0;
    final float IMG_SCALE = 2f;
    g.drawImage(ResourceManager.getImage(Tanx.TURN_START_IMG).getScaledCopy(IMG_SCALE), INSTRUCT_X, INSTRUCT_Y);
  }

  private void renderGameOver(Graphics g, Tanx bg) {
	  final float GAME_OVER_X = bg.ScreenWidth/2 - 200;
	  final float GAME_OVER_Y = 0;
    final float RESET_OFFSETX = 0;
    final float RESET_OFFSETY = 100;
    final float IMG_SCALE = 2f;


    //setup gameover screen
    Image playerWinImg;
    int winningPlayer = getWinningPlayer();
    switch(winningPlayer) {
      case 1:
        playerWinImg = ResourceManager.getImage(Tanx.PLAYER_WIN_BLUE);
        break;
      case 2:
        playerWinImg = ResourceManager.getImage(Tanx.PLAYER_WIN_RED);
        break;
      case 3:
        playerWinImg = ResourceManager.getImage(Tanx.PLAYER_WIN_GREEN);
        break;
      case 4:
        playerWinImg = ResourceManager.getImage(Tanx.PLAYER_WIN_YELLOW);
        break;
      default:
        playerWinImg = ResourceManager.getImage(Tanx.NO_WINNER_MSG);
    }
    g.drawImage(playerWinImg.getScaledCopy(IMG_SCALE), GAME_OVER_X, GAME_OVER_Y);
    g.drawImage(ResourceManager.getImage(Tanx.RESET_MSG), GAME_OVER_X + RESET_OFFSETX, GAME_OVER_Y + RESET_OFFSETY);
  }

  private int getWinningPlayer() {
    for (Player p: players) {
      if (!p.isDead()) { return p.getPlayerId(); }
    }
    return NO_WINNER_ID;
  }

  @Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
	  Tanx tg = (Tanx)game;
		Input input = container.getInput();
		Player player = players.get(pIndex);
    turnTimer -= delta;
    
    updateState(input, player, delta, tg);
    explosionSystem.update(delta);
    projectileSystem.update(delta);
    fireSystem.update();
		for(Player p: players){p.update(delta);}
    PE.update(delta);
		controlCamera(delta, input);
		world.update(delta, PE, players);
		ui.update(delta, players.get(pIndex), turnTimer, state);
		tankPointer.update(delta);
		cleanInputHandler(delta, input);
	}

  private void cleanInputHandler(int delta, Input input) {
	  cleanInputTimer += delta;
	  if (cleanInputTimer <= INPUT_TIMER_CD) {
	    input.clearKeyPressedRecord();
	    cleanInputTimer = 0;
    }
  }

  private void updateState(Input input, Player player, int delta, Tanx tg) {
  	  if (state != phase.GAMEOVER && player.getTank() == null) { changePlayer(); return; }
    if (state == phase.CHARGING) {
      if ((input.isKeyDown(Input.KEY_LSHIFT)) && turnTimer > 0){
        player.charging(delta);
      } else {
        player.fire((Projectile p) -> {
          activeProjectile = p;
          projectileSystem.addProjectile(p);
          PE.addPhysicsEntity(activeProjectile);
          camera.trackObject(activeProjectile, true);
        });
        state = phase.FIRING;
        turnTimer = FIRING_TIMEOUT;
      }
    }
    if (state == phase.MOVEFIRE){
      Tank currentTank = players.get(pIndex).getTank();
      cheatCodeHandler(input, player);
      tankPointer.pointTo(currentTank.getPosition());
      if (turnTimer <= 0){
        changePlayer();
      }
      if (input.isKeyDown(Input.KEY_D)){
        currentTank.move(Direction.RIGHT);
      } else if (input.isKeyDown(Input.KEY_A)){
        currentTank.move(Direction.LEFT);
      } else {
        currentTank.move(Direction.NONE);
      }
      if (input.isKeyDown(Input.KEY_W)){
        player.rotate(CannonDirection.UP, delta);
      } else if (input.isKeyDown(Input.KEY_S)){
        player.rotate(CannonDirection.DOWN, delta);
      }
      
      if (input.isKeyPressed(Input.KEY_F)) {
        player.nextWeapon();
      }
      if (input.isKeyPressed(Input.KEY_R)) {
        player.prevWeapon();
      }

      if (input.isKeyDown(Input.KEY_LSHIFT)) {
        state = phase.CHARGING;
      }

      if (input.isKeyDown(Input.KEY_SPACE)) {
        players.get(pIndex).useJets(delta);
      }
                                  
    } else if(state == phase.FIRING) {
      if (turnTimer <= 0) { camera.stopTracking(); changePlayer(); }
    } else if (state == phase.TURNCHANGE) {
        turnTimer = TURNLENGTH;
        camera.moveTo(player.getTank().getPosition());
      tankPointer.pointTo(player.getTank().getPosition());
        if (input.isKeyPressed(Input.KEY_ENTER)) {
          state = phase.MOVEFIRE;
          camera.trackObject(player.getTank(), false);
        }

    } else if (state == phase.GAMEOVER) {
      if (input.isKeyDown(Input.KEY_SPACE)) {
    	if(ResourceManager.getSound(Tanx.BATTLE_MUSIC).playing()) {
    		ResourceManager.getSound(Tanx.BATTLE_MUSIC).stop();
    	}
        tg.enterState(Tanx.STARTUPSTATE);
        input.clearKeyPressedRecord();
      }
    }
  }

  private void cheatCodeHandler(Input input, Player player) {
    if (input.isKeyPressed(Input.KEY_1)){
      toggleCheats = !toggleCheats;
    }
    if (toggleCheats){
      if (input.isKeyPressed(Input.KEY_2)) {
        //give player all weapons
        player.giveAllWeapons();
      }
      if (input.isKeyPressed(Input.KEY_3)) {
        //infinate jet fuel
        player.toggleInfFuel();
      }
      if (input.isKeyPressed(Input.KEY_4)) {
        //infinate health
        player.toggleInfHealth();
      }
      if (input.isKeyPressed(Input.KEY_5)) {
        //kill tank
        player.getTank().killTank();
        changePlayer();
      }
      if (input.isKeyPressed(Input.KEY_6)) {
        Tank.showDebugRays = !Tank.showDebugRays;
      }
      if (input.isKeyPressed(Input.KEY_I)) {
        Vector mouse = new Vector(input.getAbsoluteMouseX(), input.getAbsoluteMouseY());
        world.terrain.changeTerrainInCircle(camera.worldLocationForScreenLocation(mouse), 100, Terrain.TerrainType.NORMAL, Terrain.TerrainType.ICE, true);
      }
      if (input.isKeyPressed(Input.KEY_X)) {
        Vector mouse = new Vector(input.getAbsoluteMouseX(), input.getAbsoluteMouseY());
        world.terrain.changeTerrainInCircle(camera.worldLocationForScreenLocation(mouse), 100, Terrain.TerrainType.NORMAL, Terrain.TerrainType.OPEN, true);
      }
      if (input.isKeyDown(Input.KEY_T)) {
        Tank currentTank = players.get(pIndex).getTank();
        Vector mouse = new Vector(input.getAbsoluteMouseX(), input.getAbsoluteMouseY());
        currentTank.setPosition(camera.worldLocationForScreenLocation(mouse));
        currentTank.setVelocity(new Vector(0, 0));
      }
    }

  }

  private void changePlayer() {
    if (isGameOver()) {
      state = phase.GAMEOVER;
      camera.setZoom(.1f);
      turnTimer = TURNLENGTH;
      System.out.println("Game is over!");
      return;
    }
    activeProjectile = null;
    state = phase.TURNCHANGE;
    Player currentPlayer;
    do {
      pIndex ++;
      if (pIndex >= players.size()){pIndex = 0;}
      currentPlayer = players.get(pIndex);
    } while (currentPlayer.isDead());
    currentPlayer.startTurn();
    tankPointer.pointTo(currentPlayer.getTank().getPosition());
    camera.moveTo(currentPlayer.getTank().getPosition());
    fireSystem.updateTurn();
  }
  private boolean isGameOver() {
    int livingPlayersCount = 0;
    for (Player p : players) {
      if (!p.isDead()) { livingPlayersCount++; }
    }
    return livingPlayersCount < 2;
  }

  private void controlCamera(int delta, Input input) {
      if (input.isKeyPressed(Input.KEY_EQUALS)) {
        camera.setZoom(camera.getZoom() + 0.25f);
      }
      if (input.isKeyPressed(Input.KEY_MINUS)) {
        camera.setZoom(camera.getZoom() - 0.25f);
      }
      if (input.isKeyPressed(Input.KEY_7) && toggleCheats) {
        camera.toggleDebug();
      }
    camera.update(delta);
	}

	@Override
	public int getID() {
		return Tanx.PLAYINGSTATE;
	}
}
