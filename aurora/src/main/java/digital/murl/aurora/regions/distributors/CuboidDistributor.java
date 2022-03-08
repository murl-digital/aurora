package digital.murl.aurora.regions.distributors;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.regions.Region;
import digital.murl.aurora.regions.RegionCuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;

public class CuboidDistributor extends Distributor {
    private final RegionCuboid region;
    private final int xRes, yRes, zRes;
    private final boolean hollow;
    private final String formula;

    public CuboidDistributor(Region region, Map<String, Object> params) throws ClassCastException {
        this.region = (RegionCuboid)region;
        this.xRes = ((Double)params.getOrDefault("x", 1.0)).intValue();
        this.yRes = ((Double)params.getOrDefault("y", 1.0)).intValue();
        this.zRes = ((Double)params.getOrDefault("z", 1.0)).intValue();
        this.hollow = (boolean)params.getOrDefault("hollow", false);
        this.formula = (String)params.getOrDefault("formula", null);
    }

    @Override
    public Location[] distribute() {
        Location[] points = new Location[xRes * yRes * zRes - (hollow ? (xRes - 2) * (yRes - 2) * (zRes - 2) : 0)];
        World world = Bukkit.getWorld(region.worldName);
        int i = 0;
        for (double zi = 0; zi < zRes; zi++) {
            double z = (zRes > 1 ? zi / (zRes-1.0) : .5) * region.dz + region.z1;
            for (double yi = 0; yi < yRes; yi++) {
                double y = (yRes > 1 ? yi / (yRes-1.0) : .5) * region.dy + region.y1;
                for (double xi = 0; xi < xRes; xi++, i++) {
                    if (hollow) while (zi != 0 && zi != zRes-1 && yi != 0 && yi != yRes-1 && xi != 0 && xi != xRes-1) xi++;
                    double x = (xRes > 1 ? xi / (xRes-1.0) : .5) * region.dx + region.x1;
                    points[i] = new Location(world, x, y, z);
                }
            }
        }
        return points;
    }

    public static void register() {
        Aurora.registerDistributorType("CuboidGrid", "Cuboid", CuboidDistributor.class);
    }
}
