import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import jig.Vector;

public class Camera {
  
  public static float MAX_ZOOM = 4f;
  
  /// Portion of the screen that the camera/world occupy.
  /// This may not be the whole screen if we want menus outside the scrolling area.
  protected Rectangle screen;
  
  /// Floating point size of the world.
  protected Rectangle world;
  
  /// Center of the camera in unscaled world points.
  private Vector center;
  
  private float zoom;
  
  public Camera(Rectangle screen, Rectangle world) {
    this.screen = screen;
    this.world = world;
    this.center = new Vector(world.getCenter());
    this.zoom = 1.0f;
  }
  
  /// Use this to actually change the rendering.
  public void transformContext(Graphics g) {
    Vector translation = getTranslation();

    g.translate(-translation.getX()*zoom, -translation.getY()*zoom);
    g.scale(zoom, zoom);
  }
  
  public Vector worldLocationForScreenLocation(Vector screenLocation) {
    return screenLocation.add(getTranslation().scale(zoom)).scale(1/zoom);
  }
  public Vector screenLocationForWorldLocation(Vector worldLocation) {
    return worldLocation.scale(zoom).subtract(getTranslation().scale(zoom));
  }
  
  /// Center of the camera in unscaled world points.
  public Vector getCenter() { return center; }
  /// Set the center of the camera in unscaled world points.
  public void setCenter(Vector location) {
    this.center = location;
    clampViewPortToWorld();
  }
  /// Set the center of the camera by the specified unscaled world points.
  public void move(Vector worldDistance) {
    this.center = center.add(worldDistance);
    clampViewPortToWorld();
  }
  
  public float getZoom() { return this.zoom; }
  public void setZoom(float scale) {
    float fullWorldScale = screen.getWidth()/world.getWidth();
    this.zoom = Math.min(MAX_ZOOM, Math.max(fullWorldScale, scale));
    clampViewPortToWorld();
  }
  
  /// Shift center so that the whole ViewPort is within the World.
  private void clampViewPortToWorld() {
    Vector viewPortSize = viewPortSize();
    float minValidX = world.getMinX() + viewPortSize.getX()/2;
    float maxValidX = world.getMaxX() - viewPortSize.getX()/2;
    float minValidY = world.getMinY() + viewPortSize.getY()/2;
    float maxValidY = world.getMaxY() - viewPortSize.getY()/2;
    center = center.clampX(minValidX, maxValidX).clampY(minValidY, maxValidY);
  }
  
  /// The ViewPort is the Screen in world points.
  public Vector viewPortSize() {
    return new Vector(screen.getWidth()/zoom, screen.getHeight()/zoom);
  }
  /// The top left corner of the ViewPort. 
  /// The ViewPort is the Screen in world points.
  public Vector getViewPortOrigin() {
    Vector viewPortSize = viewPortSize();
    Vector camera = getCenter();
    return new Vector(camera.getX() - viewPortSize.getX()/2, camera.getY() - viewPortSize.getY()/2);
  }
  
  /// The distance from the top-left corner of the world to the top-left of the viewport.
  public Vector getTranslation() {
    return getViewPortOrigin().subtract(new Vector(world.getMinX(), world.getMinY()));
  }
  
  @Override
  public String toString() {
    return "location: " + center.toString() + ", zoom: " + zoom;
  }
}

/// When debug is true, it shows the full world and an border for the viewport.
class DebugCamera extends Camera {
  
  private boolean debug = false;
  
  public DebugCamera(Rectangle screen, Rectangle world) {
    super(screen, world);
  }
  
  public void toggleDebug() {
    this.debug = !this.debug;
  }
  
  @Override
  public void transformContext(Graphics g) {
    if (debug) {
      float toFit = screen.getWidth()/world.getWidth();
      g.scale(toFit, toFit);
      
      Vector translation = getTranslation();
      g.setColor(Color.green);
      g.drawLine(world.getMinX(), world.getMinY(), translation.getX(), translation.getY());
      g.drawRect(translation.getX(), translation.getY(), viewPortSize().getX(), viewPortSize().getY());
    } else {
      super.transformContext(g);
    }
  }
}