package fyi.sorenneedscoffee.eyecandy.util;

import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class PointUtil {
    private List<Point> points;

    public PointUtil load() {
        points = EyeCandy.dataManager.loadPoints();
        return this;
    }

    public Point getPoint(int id) {
        return points.get(points.indexOf(new Point(id, null)));
    }
}
