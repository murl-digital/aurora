package digital.murl.aurora.regions.distributors;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.regions.Region;
import digital.murl.aurora.regions.RegionCuboid;
import digital.murl.aurora.regions.RegionSphere;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;

public class SphereDistributor extends Distributor{
    private final RegionSphere region;
    private final int res;

    public SphereDistributor(Region region, Map<String, Object> params) throws ClassCastException {
        this.region = (RegionSphere)region;
        this.res = ((Double)params.getOrDefault("res", 100.0)).intValue();
    }

    @Override
    public Location[] distribute() {
        Location[] points = new Location[res];
        World world = Bukkit.getWorld(region.worldName);
        for (int i = 0; i < res; i++) {
            double k = i + .5f;

            double phi = Math.acos(1f - 2f * k / res);
            double theta = Math.PI * (1 + Math.sqrt(5)) * k;

            double x = Math.cos(theta) * Math.sin(phi) * region.r + region.x;
            double y = Math.sin(theta) * Math.sin(phi) * region.r + region.y;
            double z = Math.cos(phi) * region.r + region.z;

            points[i] = new Location(world, x, y, z);
        }
        return points;
    }

    public static void register() {
        Aurora.registerDistributorType("SphereFib", "Sphere", SphereDistributor.class);
    }
}
