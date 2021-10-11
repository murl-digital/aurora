package digital.murl.aurora.points;

import com.alibaba.fastjson.JSON;
import digital.murl.aurora.Aurora;
import org.bukkit.Location;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Points {
    private static List<Point> points;
    private static File pointsFile;

    public static void load() throws IOException {
        if (!Aurora.plugin.getDataFolder().exists())
            throw new IllegalStateException("The aurora data folder doesn't exist");

        pointsFile = new File(Aurora.plugin.getDataFolder(), "points.json");

        refresh();
    }

    public static void addPoint(Location location) {
        int length = points.size();
        // i have to do this because lambda variables have to be final.
        // thanks java.
        while (listContainsId(length)) {
            length++;
        }
        points.add(new Point(length, location.getWorld().getName(), location.getX(), location.getY(), location.getZ()));
        points.sort(null);

        new Thread(Points::savePoints).start();
    }

    public static void removePoint(int id) {
        points.remove(id);
        points.sort(null);

        new Thread(Points::savePoints).start();
    }

    public static Point getPoint(int id) {
        Point point = points.get(id);
        if (point.id == id) return point;

        return points.stream()
            .filter(p -> p.id == id)
            .findFirst()
            .orElse(null);
    }

    public static List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public static void refresh() throws IOException {
        if (!pointsFile.exists()) {
            FileWriter writer = new FileWriter(pointsFile);
            writer.write("[]");
            writer.close();
            points = Collections.synchronizedList(new LinkedList<>());

            return;
        }

        points = new CopyOnWriteArrayList<>(
            (Point[]) JSON.parseObject(new FileInputStream(pointsFile), Point[].class)
        );
        // just in case the user decides to order their points file in a funny way
        points.sort(null);
        savePoints();
    }

    private static void savePoints() {
        try {
            FileOutputStream stream = new FileOutputStream(pointsFile);
            stream.write(JSON.toJSONBytes(points));
            stream.close();
        } catch (IOException e) {
            Aurora.logger.warning("Couldn't save points to disk: " + e.getMessage());
        }
    }

    private static boolean listContainsId(int id) {
        return points.stream().anyMatch(p -> p.id == id);
    }
}
