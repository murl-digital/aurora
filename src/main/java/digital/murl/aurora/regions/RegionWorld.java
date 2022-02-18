package digital.murl.aurora.regions;

import com.google.gson.JsonObject;
import org.bukkit.Location;

public class RegionWorld extends Region {
    public RegionWorld(String id, String worldName) {
        super(id, worldName, "World");
    }

    @Override
    public boolean collisionCheck(Location location) {
        return worldName.equals(location.getWorld().getName());
    }

    @Override
    public void populateJsonObject(JsonObject object) {

    }

    public static Region loadJsonObject(JsonObject json) {
        String id = json.get("id").getAsString();
        String world = json.get("world").getAsString();
        return new RegionWorld(id, world);
    }
}
