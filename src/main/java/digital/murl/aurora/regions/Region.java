package digital.murl.aurora.regions;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public abstract class Region {
    public final String id;
    public final String worldName;

    public Region(String id, String worldName) {
        if (Bukkit.getWorld(worldName) == null)
            throw new IllegalArgumentException("The provided world does not exist");

        this.id = id;
        this.worldName = worldName;
    }

    public abstract boolean collisionCheck(Location location);

    public abstract JsonObject createJsonObject();
}
