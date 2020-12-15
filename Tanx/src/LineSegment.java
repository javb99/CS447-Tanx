import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import jig.Vector;

class LineSegment {
  public final Vector start;
  public final Vector end;
  public LineSegment(final Vector start, final Vector end) {
    this.start = start;
    this.end = end;
  }
  public static LineSegment offset(final Vector start, final Vector offset) {
    return new LineSegment(start, start.add(offset));
  }

  public void draw(Graphics g, Color color) {
    g.setColor(color);
    g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
  }

  public Vector getDifference() {
    return end.subtract(start);
  }
  public Vector getDirection() {
    return getDifference().unit();
  }
  public Vector unitNormal() {
    return getDifference().unit().getPerpendicular();
  }
  public Vector unitNormalSpecial() {
    if (start.getX() > end.getX()) {
      return unitNormal();
    } else {
      return getDifference().unit().negate().getPerpendicular();
    }
  }
  public LineSegment translate(Vector delta) {
    return new LineSegment(start.add(delta), end.add(delta));
  }
  public Vector center() {
    return start.add(end).scale(0.5f);
  }
}