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
    this.zoom = world.getWidth() / screen.getWidth();
  }
  
  void transformContext(Graphics g) {
    Vector translation = getTranslation();
    
    
//    g.translate(translation.getX(), translation.getY());
    g.scale(1/zoom, 1/zoom); // make the full world fit.
    
    g.drawLine(0, 0, translation.getX(), translation.getY());
    g.drawRect(translation.getX(), translation.getY(), screen.getWidth(), screen.getHeight());
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
    float minValidX = screen.getMinX() + screen.getWidth() / zoom;
    float maxValidX = screen.getMaxX() + screen.getWidth() / zoom;
    float minValidY = screen.getMinY() + screen.getHeight() / zoom;
    float maxValidY = screen.getMaxY() + screen.getHeight() / zoom;
    return newLocation.clampX(minValidX, maxValidX).clampY(minValidY, maxValidY);
  }
  
  public void setZoom(float scale) { this.zoom = scale; }
  
  @Override
  public String toString() {
    return "location: " + worldLocation.toString() + ", zoom: " + zoom;
  }
}