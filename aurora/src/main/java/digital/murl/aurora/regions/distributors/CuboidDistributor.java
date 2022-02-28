package digital.murl.aurora.regions.distributors;

import digital.murl.aurora.regions.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class CuboidDistributor {
    public static Location[] fillGrid(RegionCuboid region, int xRes, int yRes, int zRes) {
        Location[] points = new Location[xRes * yRes * zRes];
        World world = Bukkit.getWorld(region.worldName);
        int i = 0;
        for (double zi = 0; zi < zRes; zi++) {
            double z = (zRes > 1 ? zi / (zRes-1.0) : .5) * region.dz + region.z1;
            for (double yi = 0; yi < yRes; yi++) {
                double y = (yRes > 1 ? yi / (yRes-1.0) : .5) * region.dy + region.y1;
                for (double xi = 0; xi < xRes; xi++, i++) {
                    double x = (xRes > 1 ? xi / (xRes-1.0) : .5) * region.dx + region.x1;
                    points[i] = new Location(world, x, y, z);
                }
            }
        }
        return points;
    }

    public static Location[] fillGrid(Region region, String[] params) {
        try {
            return fillGrid((RegionCuboid)region, Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]));
        } catch (Exception e) {
            return new Location[0];
        }
    }

    public static Location[] surfaceGrid(RegionCuboid region, int xRes, int yRes, int zRes) {
        Location[] points = new Location[xRes * yRes * zRes - (xRes - 2) * (yRes - 2) * (zRes - 2)];
        World world = Bukkit.getWorld(region.worldName);
        int i = 0;
        for (double zi = 0; zi < zRes; zi++) {
            double z = (zRes > 1 ? zi / (zRes-1.0) : .5) * region.dz + region.z1;
            for (double yi = 0; yi < yRes; yi++) {
                double y = (yRes > 1 ? yi / (yRes-1.0) : .5) * region.dy + region.y1;
                for (double xi = 0; xi < xRes; xi++, i++) {
                    while (zi != 0 && zi != zRes-1 && yi != 0 && yi != yRes-1 && xi != 0 && xi != xRes-1) xi++;
                    double x = (xRes > 1 ? xi / (xRes-1.0) : .5) * region.dx + region.x1;
                    points[i] = new Location(world, x, y, z);
                }
            }
        }
        return points;
    }

    public static Location[] surfaceGrid(Region region, String[] params) {
        try {
            return surfaceGrid((RegionCuboid)region, Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]));
        } catch (Exception e) {
            return new Location[0];
        }
    }
}
