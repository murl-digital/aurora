package fyi.sorenneedscoffee.aurora.util;

import fyi.sorenneedscoffee.aurora.Aurora;

import java.util.List;

public class PointUtil {
    private List<Point> points;

    public PointUtil load() {
        points = Aurora.dataManager.loadPoints();
        return this;
    }

    public void refresh() {
        points = Aurora.dataManager.loadPoints();
    }

    public Point getPoint(int id) {
        return points.get(points.indexOf(new Point(id, null)));
    }
}
