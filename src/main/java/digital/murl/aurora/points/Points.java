package digital.murl.aurora.points;

import org.bukkit.Location;

import java.io.*;
import java.util.*;

public class Points {
    public static void load() throws IOException {
        PointManager.load();
    }

    public static int addPoint(Location location) {
        return PointManager.addPoint(location);
    }

    public static void removePoint(int id) {
        PointManager.removePoint(id);
        removePointFromGroup(id);
    }

    public static void addPointToGroup(int id, String group) {
        PointManager.addPointToGroup(id, group);
    }

    public static void removePointFromGroup(int id, String group) {
        PointManager.removePointFromGroup(id, group);
    }

    public static void removePointFromGroup(int id) {
        for (String group : PointManager.getGroups().keySet())
            removePointFromGroup(id, group);
    }

    public static void removeGroup(String group) {
        PointManager.removeGroup(group);
    }

    public static void save() {
        PointManager.save();
    }

    public static Point getPoint(int id) {
        return PointManager.getPoint(id);
    }

    public static int[] getGroupIds(String group) {
        return PointManager.getGroupIds(group);
    }

    public static Point[] getGroupPoints(String group) {
        return PointManager.getGroupPoints(group);
    }

    public static List<Point> getPoints() {
        return PointManager.getPoints();
    }

    public static Map<String, List<Integer>> getGroups() {
        return PointManager.getGroups();
    }

    public static void refresh() throws IOException {
        PointManager.refresh();
    }
}
