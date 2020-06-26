package fyi.sorenneedscoffee.aurora.util;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class Point implements Comparable<Point> {
    public final int id;
    private final Location location;

    public Point(int id, Location location) {
        this.id = id;
        this.location = location;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            return id == ((Point) obj).id;
        }

        return false;
    }

    public Location getLocation() {
        return location.clone();
    }


    @Override
    public int compareTo(@NotNull Point o) {
        return Integer.compare(id, o.id);
    }
}
