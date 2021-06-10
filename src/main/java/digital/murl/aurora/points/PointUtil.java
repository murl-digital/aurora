package digital.murl.aurora.points;

import digital.murl.aurora.Aurora;

import java.util.TreeSet;

public class PointUtil {

  private TreeSet<Point> points;

  public PointUtil load() {
    points = Aurora.dataContext.loadPoints();
    //Aurora.dataContext.savePoints(points);
    return this;
  }

  public int getAvailableId() {
    int result = points.size();
      while (points.contains(new Point(result, null))) {
          result++;
      }
    return result;
  }

  public void refresh() {
    points = Aurora.dataContext.loadPoints();
  }

  public Point getPoint(int id) {
    return points.stream().filter(p -> p.id == id).findAny().orElse(null);
  }

  public TreeSet<Point> getPoints() {
    return (TreeSet<Point>) points.clone();
  }

  public void savePoint(Point point) {
    points.add(point);
    Aurora.dataContext.savePoints(points);
  }
}
