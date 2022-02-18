package digital.murl.aurora.regions;

import com.google.gson.JsonObject;
import org.bukkit.Location;

public class RegionCuboid extends Region {
    public final double x1, y1, z1, x2, y2, z2;
    public final double dx, dy, dz;

    public RegionCuboid(String id, String worldName, double x1, double y1, double z1, double x2, double y2, double z2) {
        super(id, worldName, "Cuboid");

        this.x1 = Math.min(x1,x2);
        this.y1 = Math.min(y1,y2);
        this.z1 = Math.min(z1,z2);
        this.x2 = Math.max(x1,x2);
        this.y2 = Math.max(y1,y2);
        this.z2 = Math.max(z1,z2);
        this.dx = this.x2 - this.x1;
        this.dy = this.y2 - this.y1;
        this.dz = this.z2 - this.z1;
    }

    @Override
    public boolean collisionCheck(Location location) {
        if (!worldName.equals(location.getWorld().getName())) return false;
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        return x > x1 && x < x2 && y > y1 && y < y2 && z > z1 && z < z2;
    }

    @Override
    public void populateJsonObject(JsonObject object) {
        object.addProperty("x1", x1);
        object.addProperty("y1", y1);
        object.addProperty("z1", z1);
        object.addProperty("x2", x2);
        object.addProperty("y2", y2);
        object.addProperty("z2", z2);
    }

    public static Region loadJsonObject(JsonObject json) {
        String id = json.get("id").getAsString();
        String world = json.get("world").getAsString();
        double x1 = json.get("x1").getAsDouble();
        double y1 = json.get("y1").getAsDouble();
        double z1 = json.get("z1").getAsDouble();
        double x2 = json.get("x2").getAsDouble();
        double y2 = json.get("y2").getAsDouble();
        double z2 = json.get("z2").getAsDouble();
        return new RegionCuboid(id, world, x1, y1, z1, x2, y2, z2);
    }
}
