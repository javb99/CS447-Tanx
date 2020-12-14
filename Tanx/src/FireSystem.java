import org.newdawn.slick.Graphics;

import java.util.ArrayList;

public class FireSystem {

    private ArrayList<GroundFire> fires;
    private ArrayList<GroundFire> toAdd;

    public FireSystem() {
        fires = new ArrayList<GroundFire>();
        toAdd = new ArrayList<GroundFire>();
    }

    public void addFire(GroundFire fire) { toAdd.add(fire); }
    public void updateTurn() {
        for (GroundFire f: fires) {
            f.updateTurn();
        }
        fires.removeIf(f -> f.getIsDead() );
        fires.addAll(toAdd);
        toAdd.clear();
    }

    public void render(Graphics g) {
        fires.forEach(f -> f.render(g));
    }
}
