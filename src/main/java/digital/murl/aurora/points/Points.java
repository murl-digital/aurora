package digital.murl.aurora.points;

import digital.murl.aurora.Aurora;
import org.bukkit.Location;

import java.io.*;
import java.util.*;

public class Points {
    private static List<Point> points;
    private static Map<String, List<Integer>> groups;
    private static File pointsFile;
    private static int pointCursor;

    public static void load() throws IOException {
        if (!Aurora.plugin.getDataFolder().exists())
            throw new IllegalStateException("The aurora data folder doesn't exist");

        pointsFile = new File(Aurora.plugin.getDataFolder(), "points.csv");

        refresh();
    }

    public static void addPoint(Location location) {
        while (points.stream().anyMatch(p -> p == null ? false : p.id == pointCursor))
            pointCursor++;

        if (pointCursor < points.size()) points.set(pointCursor, new Point(pointCursor, location.getWorld().getName(), location.getX(), location.getY(), location.getZ()));
        else points.add(new Point(pointCursor, location.getWorld().getName(), location.getX(), location.getY(), location.getZ()));

        new Thread(Points::savePoints).start();
    }

    public static void removePoint(int id) {
        if (id > points.size()-1) return;
        points.set(id,null);
        for (String group : groups.keySet())
            groups.get(group).removeIf(p -> p == id);
        if (id < pointCursor) pointCursor = id;
        //The lambda function for removing points from groups overlaps with the threaded save points function so can't do that I guess
        //new Thread(Points::savePoints).start();
        savePoints();
    }

    public static Point getPoint(int id) {
        return id < points.size() && id > -1 ? points.get(id) : null;
    }

    public static List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public static Map<String, List<Integer>> getGroups() {
        return Collections.unmodifiableMap(groups);
    }

    public static void refresh() throws IOException {
        points = Collections.synchronizedList(new LinkedList<>());
        groups = new HashMap<>();

        if (!pointsFile.exists()) {
            try {
                FileWriter writer = new FileWriter(pointsFile);
                writer.close();
            } catch (IOException e){
                Aurora.logger.warning("Couldn't create points file: " + e.getMessage());
            }
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(pointsFile));
            pointCursor = -1;
            int id = -1;
            String row = null;
            while ((row = reader.readLine()) != null) {
                id++;

                if (row.isEmpty()) {
                    if (pointCursor < 0) pointCursor = id;
                    points.add(null);
                    continue;
                }

                String[] data = row.split(",");
                if (data.length < 4) {
                    Aurora.logger.warning("Missing data for point " + id);
                    points.add(null);
                    continue;
                }

                try {
                    points.add(new Point(
                        id,
                        data[0],
                        Double.parseDouble(data[1]),
                        Double.parseDouble(data[2]),
                        Double.parseDouble(data[3])
                    ));
                } catch (Exception e) {
                    Aurora.logger.warning("Encountered an error while parsing data for point " + id);
                    points.add(null);
                    continue;
                }

                for (int i = 4; i < data.length; i++) {
                    if (groups.containsKey(data[i]))
                        groups.get(data[i]).add(id);
                    else groups.put(data[i], new ArrayList<>(Arrays.asList(id)));
                }
            }
            reader.close();

            if (pointCursor < 0) pointCursor = id < 0 ? 0 : id;
            savePoints();

        } catch (IOException e) {
            Aurora.logger.warning("Couldn't load points from disk: " + e.getMessage());
        } catch (Exception e) {
            Aurora.logger.warning("Something unexpected happened: " + e.getMessage());
        }
    }

    private static void savePoints() {
        List<String> csv = new ArrayList<>();
        for (Point point : points)
            if (point == null) csv.add("");
            else csv.add(String.format(Locale.US,"%s,%f,%f,%f",point.worldName,point.x,point.y,point.z));
        for (String group : groups.keySet()) {
            for (int point : groups.get(group)) {
                csv.set(point, csv.get(point) + ',' + group);
            }
        }
        String data = "";
        for (String r : csv)
            data += r + '\n';

        try {
            FileWriter writer = new FileWriter(pointsFile);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            Aurora.logger.warning("Couldn't save points to disk: " + e.getMessage());
        }
    }
}
