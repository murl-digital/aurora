package digital.murl.aurora.regions;

import com.google.gson.JsonObject;
import org.bukkit.Location;

public class RegionSphere extends Region {
    public final double x, y, z, r;

    public RegionSphere(String id, String worldName, double x, double y, double z, double r) {
        super(id, worldName);

        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
    }

    @Override
    public boolean collisionCheck(Location location) {
        if (!worldName.equals(location.getWorld().getName())) return false;
        double x = (location.getX() - this.x) / r;
        double y = (location.getY() - this.y) / r;
        double z = (location.getZ() - this.z) / r;
        return (x*x + y*y + z*z) < 1.0;
    }

    @Override
    public JsonObject createJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("RegionType", "Sphere");
        json.addProperty("id", id);
        json.addProperty("world", worldName);
        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("z", z);
        json.addProperty("r", r);
        return json;
    }

    public static Region loadJsonObject(JsonObject json) {
        String id = json.get("id").getAsString();
        String world = json.get("world").getAsString();
        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        double r = json.get("r").getAsDouble();
        return new RegionSphere(id, world, x, y, z, r);
    }
}
