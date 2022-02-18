package digital.murl.aurora.regions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

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
    public void populateJsonObject(Map<String, Object> data) {
        data.put("x", x);
        data.put("y", y);
        data.put("z", z);
        data.put("r", r);
    }

    public static Region jsonConstructor(Map<String, Object> data) {
        String id = (String)data.get("id");
        String world = (String)data.get("world");
        double x = (Double)data.get("x");
        double y = (Double)data.get("y");
        double z = (Double)data.get("z");
        double r = (Double)data.get("r");
        return new RegionSphere(id, world, x, y, z, r);
    }

    public static RegionParameterConstructor.Result parameterConstructor(Player sender, String[] params) {
        if (params.length != 6)
            return RegionParameterConstructor.Result.WRONG_SYNTAX;

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
            return new RegionParameterConstructor.Result(false, "Couldn't parse coordinates.");
        }
        double r;
        try {
            r = Double.parseDouble(params[5]);
        } catch (Exception e) {
            return new RegionParameterConstructor.Result (false, "Couldn't parse radius.");
        }
        Regions.addRegion(new RegionSphere(params[0], sender.getLocation().getWorld().getName(), p[0], p[1], p[2], r));
        Regions.save();

        return RegionParameterConstructor.Result.SUCCESS;
    }

    public static String[] parameterCompleter(Player sender, String[] params) {
        Location location = sender.getLocation();
        if (params.length == 3)
            return new String[] {String.format("%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ()), "~ ~ ~"};
        if (params.length == 4)
            return new String[] {String.format("%d %d", location.getBlockY(), location.getBlockZ()), "~ ~"};
        if (params.length == 5)
            return new String[] {String.format("%d", location.getBlockZ()), "~"};
        return null;
    }
}
