package digital.murl.aurora.points;

import org.bukkit.Location;

import java.io.*;
import java.util.*;

public class Points {
    public static void load() throws IOException {
        PointManager.load();
    }

    public static int add(Location location) {
        return PointManager.addPoint(location);
    }

    public static int[] add(Location[] locations) {
        return Arrays.stream(locations).mapToInt((location) -> PointManager.addPoint(location)).toArray();
    }

    public static void remove(int id) {
        PointManager.removePoint(id);
        removePointFromGroup(id);
    }

    public static void remove(int[] ids) {
        for (int id : ids)
            remove(id);
    }

    public static void remove(String input) {
        try {
            int id = Integer.parseInt(input);
            PointManager.removePoint(id);
            removePointFromGroup(id);
        } catch (Exception e) {
            remove(PointManager.getGroupIds(input));
        }
    }

    public static void remove(String[] input) {
        for (String item : input)
            remove(item);
    }

    public static void remove(Object input) {
        try {
            remove((int)input);
        } catch (Exception e) {
            try {
                remove((String)input);
            } catch (Exception e2) {

            }
        }
    }

    public static void remove(Object[] input) {
        for (Object item : input)
            remove(item);
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

    public static int[] getId(String input) {
        try {
            return new int[] {Integer.parseInt(input)};
        } catch (Exception e) {
            if (input.contains(","))
                return getId(input.split(","));
            return PointManager.getGroupIds(input);
        }
    }

    public static int[] getId(String[] input) {
        List<Integer> ids = new LinkedList<>();
        for (String item : input)
            for (int id : getId(item))
                ids.add(id);
        int[] array = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++)
            array[i] = ids.get(i);
        return array;
    }

    public static int[] getId(Object input) {
        try {
            return new int[]{((Double)input).intValue()};
        } catch (Exception e) {
            try {
                return getId((String)input);
            } catch (Exception e2) {
                try {
                    return getId(((List)input).toArray());
                } catch (Exception e3) {
                    return new int[0];
                }
            }
        }
    }

    public static int[] getId(Object[] input) {
        List<Integer> ids = new LinkedList<>();
        for (Object item : input)
            for (int id : getId(item))
                ids.add(id);
        int[] array = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++)
            array[i] = ids.get(i);
        return array;
    }

    public static Point getPoint(int id) {
        return PointManager.getPoint(id);
    }

    public static Point[] getPoint(int[] ids) {
        List<Point> points = new LinkedList<>();
        for (int id : ids) {
            Point point = PointManager.getPoint(id);
            if (point == null || points.contains(point)) continue;
            points.add(point);
        }
        Point[] array = new Point[points.size()];
        for (int i = 0; i < points.size(); i++)
            array[i] = points.get(i);
        return array;
    }

    public static Point[] getPoint(String item) {
        return getPoint(getId(item));
    }

    public static Point[] getPoint(String[] item) {
        return getPoint(getId(item));
    }

    public static Point[] getPoint(Object item) {
        return getPoint(getId(item));
    }

    public static Point[] getPoint(Object[] item) {
        return getPoint(getId(item));
    }

    public static Location getPointLocation(Point point) {
        return point == null ? null : point.location();
    }

    public static Location[] getPointLocation(Point[] points) {
        List<Location> locations = new LinkedList<>();
        for (Point point : points)
            locations.add(getPointLocation(point));
        Location[] array = new Location[locations.size()];
        for (int i = 0; i < locations.size(); i++)
            array[i] = locations.get(i);
        return array;
    }

    public static Location getPointLocation(int id) {
        return getPointLocation(getPoint(id));
    }

    public static Location[] getPointLocation(int[] ids) {
        return getPointLocation(getPoint(ids));
    }

    public static Location[] getPointLocation(String item) {
        return getPointLocation(getPoint(item));
    }

    public static Location[] getPointLocation(String[] item) {
        return getPointLocation(getPoint(item));
    }

    public static Location[] getPointLocation(Object item) {
        return getPointLocation(getPoint(item));
    }

    public static Location[] getPointLocation(Object[] item) {
        return getPointLocation(getPoint(item));
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
