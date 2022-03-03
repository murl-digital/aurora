package digital.murl.aurora.regions.distributors;

import digital.murl.aurora.regions.Region;
import digital.murl.aurora.regions.RegionCuboid;
import digital.murl.aurora.regions.RegionSphere;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class SphereDistributor {
    public static Location[] surfaceFibonacci(RegionSphere region, int amount) {
        Location[] points = new Location[amount];
        World world = Bukkit.getWorld(region.worldName);
        for (int i = 0; i < amount; i++) {
            double k = i + .5f;

            double phi = Math.acos(1f - 2f * k / amount);
            double theta = Math.PI * (1 + Math.sqrt(5)) * k;

            double x = Math.cos(theta) * Math.sin(phi) * region.r + region.x;
            double y = Math.sin(theta) * Math.sin(phi) * region.r + region.y;
            double z = Math.cos(phi) * region.r + region.z;

            points[i] = new Location(world, x, y, z);
        }
        return points;
    }

    public static Location[] surfaceFibonacci(Region region, String[] params) {
        try {
            return surfaceFibonacci((RegionSphere)region, Integer.parseInt(params[0]));
        } catch (Exception e) {
            return new Location[0];
        }
    }
}
