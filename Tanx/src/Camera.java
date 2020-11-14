import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import jig.Vector;

class Camera {
  /// Portion of the screen that the camera/world occupy.
  /// This may not be the whole screen if we want menus outside the scrolling area.
  Rectangle screen;
  
  /// Floating point size of the world.
  Rectangle world;
  
  /// Center of the camera in unscaled world points.
  private Vector worldLocation;
  
  private float zoom;
  
  private boolean debug = false;
  
  Camera(Rectangle screen, Rectangle world) {
    this.screen = screen;
    this.world = world;
    this.worldLocation = new Vector(world.getCenter());
    this.zoom = 1.0f;
  }
  
  public void toggleDebug() {
    this.debug = !this.debug;
  }
  
  void transformContext(Graphics g) {
    Vector translation = getTranslation();
    
    if (!debug) {
      g.translate(-translation.getX()*zoom, -translation.getY()*zoom);
      g.scale(zoom, zoom);
    } else {
      float toFit = screen.getWidth()/world.getWidth();
      g.scale(toFit, toFit);
      
      g.setColor(Color.green);
      g.drawLine(world.getMinX(), world.getMinY(), translation.getX(), translation.getY());
      g.drawRect(translation.getX(), translation.getY(), viewPortSize().getX(), viewPortSize().getY());
    }
  }
  
  /// Screen size in world size points.
  public Vector viewPortSize() {
    return new Vector(screen.getWidth()/zoom, screen.getHeight()/zoom);
  }
  public Vector viewPortOrigin() {
    Vector viewPortSize = viewPortSize();
    Vector camera = getWorldLocation();
    return new Vector(camera.getX() - viewPortSize.getX()/2, camera.getY() - viewPortSize.getY()/2);
  }
  
  /// The distance from the top-left corner of the world to the top-left of the viewport.
  public Vector getTranslation() {
    return viewPortOrigin().subtract(new Vector(world.getMinX(), world.getMinY()));
  }
  
  public Vector getWorldLocation() { return worldLocation; }
  public void setWorldLocation(Vector location) {
    this.worldLocation = clampToScreen(location);
  }
  private Vector clampToScreen(Vector newCenterLocation) {
    Vector viewPortSize = viewPortSize();
    float minValidX = world.getMinX() + viewPortSize.getX()/2;
    float maxValidX = world.getMaxX() - viewPortSize.getX()/2;
    float minValidY = world.getMinY() + viewPortSize.getY()/2;
    float maxValidY = world.getMaxY() - viewPortSize.getY()/2;
    return newCenterLocation.clampX(minValidX, maxValidX).clampY(minValidY, maxValidY);
  }
  
  public float getZoom() { return this.zoom; }
  public void setZoom(float scale) { this.zoom = scale; }
  
  public Vector worldLocationForScreenLocation(Vector screenLocation) {
    return screenLocation.add(getTranslation().scale(zoom)).scale(1/zoom);
  }
  public Vector screenLocationForWorldLocation(Vector worldLocation) {
    return worldLocation.scale(zoom).subtract(getTranslation().scale(zoom));
  }
  
  @Override
  public String toString() {
    return "location: " + worldLocation.toString() + ", zoom: " + zoom;
  }
}