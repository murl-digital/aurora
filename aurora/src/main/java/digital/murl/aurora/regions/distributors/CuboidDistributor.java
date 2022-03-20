package digital.murl.aurora.regions.distributors;

import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.eval.parser.Parser;
import com.dfsek.paralithic.eval.parser.Scope;
import digital.murl.aurora.Aurora;
import digital.murl.aurora.regions.Region;
import digital.murl.aurora.regions.RegionCuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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
    List<String> expressionTypes = null;
    List<Expression> expressions = null;

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

        if (params.containsKey("expressions")) {
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

            expressions = new LinkedList<>();
            expressionTypes = new LinkedList<>();

            List<Map<String, Object>> expressionList = (List<Map<String, Object>>)params.get("expressions");
            for (Map<String, Object> expressionItem : expressionList) {
                try {
                    Expression expression = parser.parse((String)expressionItem.get("expression"), scope);
                    String expressionType = (String)expressionItem.get("type");
                    expressions.add(expression);
                    expressionTypes.add(expressionType);
                } catch (Exception e) {

                }
            }
        }
        this.startTime = System.currentTimeMillis() / 1000;
    }

    @Override
    public Location[] distribute() {
        List<Location> points = new LinkedList<>();

        double min = Double.min(Double.min(region.dx,region.dy),region.dz);
        double dx = (region.dx / min);
        double dy = (region.dy / min);
        double dz = (region.dz / min);

        double t = System.currentTimeMillis() / 1000 - startTime;

        int i = 0;
        for (double zi = 0; zi < zRes; zi++) {
            double z = (zRes > 1 ? zi / (zRes-1.0) : .5);
            for (double yi = 0; yi < yRes; yi++) {
                double y = (yRes > 1 ? yi / (yRes-1.0) : .5);
                for (double xi = 0; xi < xRes; xi++, i++) {
                    if (hollow) while (zi != 0 && zi != zRes-1 && yi != 0 && yi != yRes-1 && xi != 0 && xi != xRes-1) xi++;
                    double x = (xRes > 1 ? xi / (xRes-1.0) : .5);

                    Location location = evaluateExpressions(x,y,z,dx,dy,dz,i,t);

                    if (location != null)
                        points.add(location);
                }
            }
        }

        Location[] array = new Location[points.size()];
        for (i = 0; i < points.size(); i++)
            array[i] = points.get(i);

        return array;
    }

    private Location evaluateExpressions(double x, double y, double z, double dx, double dy, double dz, double index, double time) {
        double wx = x * region.dx + region.x1;
        double wy = y * region.dy + region.y1;
        double wz = z * region.dz + region.z1;

        if (expressions == null)
            return new Location(Bukkit.getWorld(region.worldName), wx, wy, wz);

        double lx = (x * 2 - 1) * dx;
        double ly = (y * 2 - 1) * dy;
        double lz = (z * 2 - 1) * dz;

        for (int i = 0; i < expressions.size(); i++) {
            switch (expressionTypes.get(i)) {
                case "mask":
                    if (expressions.get(i).evaluate(x, y, z, lx, ly, lz, wx, wy, wz, index, time) <= 0)
                        return null;
                    break;
                case "map_lx":
                    lx = expressions.get(i).evaluate(x, y, z, lx, ly, lz, wx, wy, wz, index, time);
                    x = ((lx / dx) + 1) / 2;
                    wx = x * region.dx + region.x1;
                    break;
                case "map_ly":
                    ly = expressions.get(i).evaluate(x, y, z, lx, ly, lz, wx, wy, wz, index, time);
                    y = ((ly / dy) + 1) / 2;
                    wy = y * region.dy + region.y1;
                    break;
                case "map_lz":
                    lz = expressions.get(i).evaluate(x, y, z, lx, ly, lz, wx, wy, wz, index, time);
                    z = ((lz / dz) + 1) / 2;
                    wz = z * region.dz + region.z1;
                case "map_x":
                    x = expressions.get(i).evaluate(x, y, z, lx, ly, lz, wx, wy, wz, index, time);
                    lx = (x * 2 - 1) * dx;
                    wx = x * region.dx + region.x1;
                    break;
                case "map_y":
                    y = expressions.get(i).evaluate(x, y, z, lx, ly, lz, wx, wy, wz, index, time);
                    ly = (y * 2 - 1) * dy;
                    wy = y * region.dy + region.y1;
                    break;
                case "map_z":
                    z = expressions.get(i).evaluate(x, y, z, lx, ly, lz, wx, wy, wz, index, time);
                    lz = (z * 2 - 1) * dz;
                    wz = z * region.dz + region.z1;
                    break;
                default:
                    return null;
            }
        }
        return new Location(Bukkit.getWorld(region.worldName), wx, wy, wz);
    }

    public static void register() {
        Aurora.registerDistributorType("CuboidGrid", "Cuboid", CuboidDistributor.class);
    }
}
