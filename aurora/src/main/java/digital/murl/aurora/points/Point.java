package digital.murl.aurora.points;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class Point implements Comparable<Point> {
    public final int id;
    public final String worldName;
    public final double x, y, z;

    public Point(int id, String worldName, double x, double y, double z) {
        if (Bukkit.getWorld(worldName) == null)
            throw new IllegalArgumentException("The provided world does not exist");

        this.id = id;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location location() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    @Override
    public int compareTo(@NotNull Point p) {
        return Integer.compare(id, p.id);
    }
}
