import jig.Vector;

public class MiniBomb extends Projectile {

	private ClusterProjectile parent;
	
	public MiniBomb(float x, float y, Vector v, int r, int d, ClusterProjectile parent) {
		super(x, y, v, r, d);
		this.parent = parent;
		TI = Projectile.TerrainInteraction.CLUSTER;
	}
	
	public ClusterProjectile getParent() {
		return parent;
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		if(this.getIsDead()) {
			this.parent.getBombList().remove(this);
		}
	}
}
