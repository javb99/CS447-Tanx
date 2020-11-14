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
  
  Camera(Rectangle screen, Rectangle world) {
    this.screen = screen;
    this.world = world;
    this.worldLocation = new Vector(world.getCenter());
    this.zoom = 1.0f;
  }
  
  void transformContext(Graphics g) {
    Vector translation = getTranslation();
    
    
    g.translate(-translation.getX(), -translation.getY());
//    g.scale(zoom, zoom);
    
//    g.setColor(Color.green);
//    g.drawLine(0, 0, translation.getX(), translation.getY());
//    g.drawRect(translation.getX(), translation.getY(), screen.getWidth(), screen.getHeight());
  }
  
  /// The distance from the top-left corner of the world to the top-left of the viewport.
  public Vector getTranslation() {
    float screenCenterX = screen.getCenterX();
    float screenCenterY = screen.getCenterY();
    Vector camera = getWorldLocation();
    return new Vector(camera.getX() - screenCenterX, camera.getY() - screenCenterY);
  }
  
  public Vector getWorldLocation() { return worldLocation; }
  public void setWorldLocation(Vector location) {
    this.worldLocation = clampToScreen(location);
  }
  private Vector clampToScreen(Vector newLocation) {
    float minValidX = world.getMinX() + screen.getWidth() / zoom;
    float maxValidX = world.getMaxX() - screen.getWidth() / zoom;
    float minValidY = world.getMinY() + screen.getHeight() / zoom;
    float maxValidY = world.getMaxY() - screen.getHeight() / zoom;
    return newLocation.clampX(minValidX, maxValidX).clampY(minValidY, maxValidY);
  }
  
  public void setZoom(float scale) { this.zoom = scale; }
  
  public Vector worldLocationForScreenLocation(Vector screenLocation) {
    return screenLocation.add(getTranslation());
  }
  public Vector screenLocationForWorldLocation(Vector worldLocation) {
    return worldLocation.subtract(getTranslation());
  }
  
  @Override
  public String toString() {
    return "location: " + worldLocation.toString() + ", zoom: " + zoom + "screen: " + screenLocationForWorldLocation(worldLocation);
  }
}