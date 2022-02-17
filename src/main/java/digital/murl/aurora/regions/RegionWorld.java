package digital.murl.aurora.regions;

import com.google.gson.JsonObject;
import org.bukkit.Location;

public class RegionWorld extends Region {
    public RegionWorld(String id, String worldName) {
        super(id, worldName);
    }

    @Override
    public boolean collisionCheck(Location location) {
        return worldName.equals(location.getWorld().getName());
    }

    @Override
    public JsonObject createJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("RegionType", "World");
        json.addProperty("id", id);
        json.addProperty("world", worldName);
        return json;
    }

    public static Region loadJsonObject(JsonObject json) {
        String id = json.get("id").getAsString();
        String world = json.get("world").getAsString();
        return new RegionWorld(id, world);
    }
}
