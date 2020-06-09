package fyi.sorenneedscoffee.eyecandy.util;

import org.bukkit.Location;

public class Point {
    public int id;
    public Location location;

    public Point(int id, Location location) {
        this.id = id;
        this.location = location;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Point) {
            return id == ((Point) obj).id;
        }

        return false;
    }
}
