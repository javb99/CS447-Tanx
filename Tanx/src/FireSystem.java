import org.newdawn.slick.Graphics;

import java.util.ArrayList;

public class FireSystem {

    private ArrayList<GroundFire> fires;
    private ArrayList<GroundFire> toAdd;
    private Terrain terrain;

    public FireSystem(Terrain t) {
        fires = new ArrayList<GroundFire>();
        toAdd = new ArrayList<GroundFire>();
        terrain = t;
    }

    public void addFire(GroundFire fire) { toAdd.add(fire); }

    public void updateTurn() {
        for (GroundFire f: fires) {
            f.updateTurn(terrain);
        }
        terrain.applyMask();
    }

    public void update() {
        fires.removeIf(f -> f.getIsDead() );
        fires.addAll(toAdd);
        toAdd.clear();
    }

    public void render(Graphics g) {
        fires.forEach(f -> f.render(g));
    }
    
   
}
