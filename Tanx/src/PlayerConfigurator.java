import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.newdawn.slick.Color;

import jig.Vector;

public class PlayerConfigurator {

	private final float TANK_SPACING = 250;
	private final float EDGE_SPACING = 25;
	
	
	private int width;

	private int numPlayers;
	private int numTanks;
	private Color[] colors = {Color.blue, Color.red, Color.green, Color.yellow};
	
	public PlayerConfigurator(int width, int numPlayers, int numTanks) {
		this.numPlayers = numPlayers;
		this.numTanks = numTanks;
		this.width = width;
		
		if(this.numPlayers > 4) this.numPlayers = 4;
		if(this.numPlayers < 2) this.numPlayers = 2;
	}
	
	public void setNumPlayers(int p) {
		numPlayers = p;
	}
	
	public void setNumTanks(int t) {
		numTanks = t;
	}
	
	public int getNumPlayers() {return numPlayers;}
	public int getNumTanks() {return numTanks;}
	
	public ArrayList<Player> config() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		ArrayList<Tank> tankList = new ArrayList<Tank>();
		Date d = new Date();
		Random rand = new Random(d.getTime());
		
		for(int i = 1; i <= numPlayers; i++) {
			Player p = new Player(colors[i-1], i);
			
			while(p.getTanks().size() < numTanks) {
				Tank t = p.addTank(rand.nextFloat()*width, 100);
				while(t.getCoarseGrainedMinX() < EDGE_SPACING) {
					t.translate(new Vector(1, 0));
				}
				while(t.getCoarseGrainedMaxX() > width - EDGE_SPACING) {
					t.translate(new Vector(-1, 0));
				}
				if(!checkIfOpen(t, tankList)) {
					p.removeTank(t);
				} else {
					tankList.add(t);
				}
			}
			
			playerList.add(p);
		}
		
		return playerList;
	}
	
	private boolean checkIfOpen(Tank newTank, ArrayList<Tank> oldTanks) {
		for(int i = 0; i < oldTanks.size(); i++) {
			if(oldTanks.get(i).getPosition().distance(newTank.getPosition()) < TANK_SPACING) {
				return false;
			}
		}
		return true;
	}
}
