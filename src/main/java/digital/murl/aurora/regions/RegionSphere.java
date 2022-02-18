package digital.murl.aurora.regions;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;

public class RegionSphere extends Region {
    public final double x, y, z, r;

    public RegionSphere(String id, String worldName, double x, double y, double z, double r) {
        super(id, worldName, "Sphere");
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
    public void populateJsonObject(JsonObject object) {
        object.addProperty("x", x);
        object.addProperty("y", y);
        object.addProperty("z", z);
        object.addProperty("r", r);
    }

    public static Region jsonConstructor(JsonObject json) {
        String id = json.get("id").getAsString();
        String world = json.get("world").getAsString();
        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        double r = json.get("r").getAsDouble();
        return new RegionSphere(id, world, x, y, z, r);
    }

    public static CommandResult parameterConstructor(Player sender, String[] params) {
        if (params.length != 6)
            return CommandResult.WRONG_SYNTAX;

        double[] p = new double[4];
        try {
            Location l = sender.getLocation();
            double[] sp = new double[] {l.getX(),l.getY(),l.getZ()};
            for (int i = 0; i < 3; i++) {
                if (params[i+2].equals("~"))
                    p[i] = sp[i];
                else if (params[i+2].charAt(0) == '~')
                    p[i] = sp[i] + Double.parseDouble(params[i+2].substring(1));
                else
                    p[i] = Double.parseDouble(params[i+2]);
            }
        } catch (Exception e) {
            return CommandResult.from(CommandResult.Type.FAILURE, "Couldn't parse coordinates.");
        }
        double r;
        try {
            r = Double.parseDouble(params[5]);
        } catch (Exception e) {
            return CommandResult.from(CommandResult.Type.FAILURE, "Couldn't parse radius.");
        }
        Regions.addRegion(new RegionSphere(params[0], sender.getLocation().getWorld().getName(), p[0], p[1], p[2], r));
        Regions.save();

        return CommandResult.SUCCESS;
    }

    public static TabResult parameterCompleter(Player sender, String[] params) {
        Location location = sender.getLocation();
        if (params.length == 3)
            return TabResult.of("",
                String.format("%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ()), "~ ~ ~");
        if (params.length == 4)
            return TabResult.of("",
                String.format("%d %d", location.getBlockY(), location.getBlockZ()), "~ ~");
        if (params.length == 5)
            return TabResult.of("",
                String.format("%d", location.getBlockZ()), "~");
        return TabResult.EMPTY_RESULT;
    }
}
