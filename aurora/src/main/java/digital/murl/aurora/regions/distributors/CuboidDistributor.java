package digital.murl.aurora.regions.distributors;

import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.eval.parser.Parser;
import com.dfsek.paralithic.eval.parser.Scope;
import digital.murl.aurora.Aurora;
import digital.murl.aurora.regions.Region;
import digital.murl.aurora.regions.RegionCuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CuboidDistributor extends Distributor {
    private RegionCuboid region;
    private int xRes, yRes, zRes;
    private boolean hollow;
    private long startTime;

    Parser parser = new Parser();
    Scope scope = new Scope();
    Expression expression = null;

    public CuboidDistributor(Region region, Map<String, Object> params) throws ClassCastException {
        this.region = (RegionCuboid)region;
        if (params.containsKey("distribute")) {
            double d = (Double)params.get("distribute");
            this.xRes = ((Double)(this.region.dx / d)).intValue() + 1;
            this.yRes = ((Double)(this.region.dy / d)).intValue() + 1;
            this.zRes = ((Double)(this.region.dz / d)).intValue() + 1;
        } else {
            this.xRes = 1;
            this.yRes = 1;
            this.zRes = 1;
        }
        if (params.containsKey("x")) this.xRes = ((Double)params.get("x")).intValue();
        if (params.containsKey("y")) this.yRes = ((Double)params.get("y")).intValue();
        if (params.containsKey("z")) this.zRes = ((Double)params.get("z")).intValue();
        this.hollow = (boolean)params.getOrDefault("hollow", false);

        if (params.containsKey("expression")) {
            scope.addInvocationVariable("nx");
            scope.addInvocationVariable("ny");
            scope.addInvocationVariable("nz");
            scope.addInvocationVariable("lx");
            scope.addInvocationVariable("ly");
            scope.addInvocationVariable("lz");
            scope.addInvocationVariable("wx");
            scope.addInvocationVariable("wy");
            scope.addInvocationVariable("wz");
            scope.addInvocationVariable("i");
            scope.addInvocationVariable("t");
            try {
                expression = parser.parse((String) params.get("expression"), scope);
            } catch (Exception e) {
                expression = null;
            }
        }
        this.startTime = System.currentTimeMillis() / 1000;
    }

    @Override
    public Location[] distribute() {
        List<Location> points = new LinkedList<>();
        World world = Bukkit.getWorld(region.worldName);

        double min = Double.min(Double.min(region.dx,region.dy),region.dz);
        double dx = (region.dx / min);
        double dy = (region.dy / min);
        double dz = (region.dz / min);

        double t = System.currentTimeMillis() - startTime / 1000;

        int i = 0;
        for (double zi = 0; zi < zRes; zi++) {
            double z = (zRes > 1 ? zi / (zRes-1.0) : .5);
            for (double yi = 0; yi < yRes; yi++) {
                double y = (yRes > 1 ? yi / (yRes-1.0) : .5);
                for (double xi = 0; xi < xRes; xi++, i++) {
                    if (hollow) while (zi != 0 && zi != zRes-1 && yi != 0 && yi != yRes-1 && xi != 0 && xi != xRes-1) xi++;
                    double x = (xRes > 1 ? xi / (xRes-1.0) : .5);

                    double lx = (x*2-1) * dx;
                    double ly = (y*2-1) * dy;
                    double lz = (z*2-1) * dz;

                    double wx = x * region.dx + region.x1;
                    double wy = y * region.dy + region.y1;
                    double wz = z * region.dz + region.z1;

                    if (expression == null)
                        points.add(new Location(world, wx, wy, wz));
                    else if (expression.evaluate(x, y, z, lx, ly, lz, wx, wy, wz, i, t) > 0)
                        points.add(new Location(world, wx, wy, wz));
                }
            }
        }

        Location[] array = new Location[points.size()];
        for (i = 0; i < points.size(); i++)
            array[i] = points.get(i);

        return array;
    }

    public static void register() {
        Aurora.registerDistributorType("CuboidGrid", "Cuboid", CuboidDistributor.class);
    }
}
