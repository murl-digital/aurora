package fyi.sorenneedscoffee.aurora.util;

import fyi.sorenneedscoffee.aurora.Aurora;

import java.util.TreeSet;

public class PointUtil {
    private TreeSet<Point> points;

    public PointUtil load() {
        points = Aurora.dataManager.loadPoints();
        Aurora.dataManager.savePoints(points);
        return this;
    }

    public int getAvailableId() {
        int result = points.size();
        while (points.contains(new Point(result, null)))
            result++;
        return result;
    }

    public void refresh() {
        points = Aurora.dataManager.loadPoints();
    }

    public Point getPoint(int id) {
        return points.stream().filter(p -> p.id == id).findAny().orElse(null);
    }

    public TreeSet<Point> getPoints() {
        return (TreeSet<Point>) points.clone();
    }

    public void savePoint(Point point) {
        points.add(point);
        Aurora.dataManager.savePoints(points);
    }
}
