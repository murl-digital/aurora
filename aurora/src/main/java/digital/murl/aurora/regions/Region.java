package digital.murl.aurora.regions;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Map;

public abstract class Region {
    public final String id;
    public final String worldName;
    public final String type;

    public Region(String id, String worldName, String type) {
        if (Bukkit.getWorld(worldName) == null)
            throw new IllegalArgumentException("The provided world does not exist");

        this.id = id;
        this.worldName = worldName;
        this.type = type;
    }

    public abstract boolean collisionCheck(Location location);

    public abstract void populateMap(Map<String, Object> data);
}
