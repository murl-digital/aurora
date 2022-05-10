package digital.murl.aurora.regions.distributors;

import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.eval.parser.Parser;
import com.dfsek.paralithic.eval.parser.Scope;
import digital.murl.aurora.Aurora;
import digital.murl.aurora.regions.Region;
import digital.murl.aurora.regions.RegionSphere;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SphereDistributor extends Distributor{
    private final RegionSphere region;
    private final int res;
    private long startTime;

    Parser parser = new Parser();
    Scope scope = new Scope();
    List<String> expressionTypes = null;
    List<Expression> expressions = null;

    public SphereDistributor(Region region, Map<String, Object> params) throws ClassCastException {
        this.region = (RegionSphere)region;
        this.res = ((Double)params.getOrDefault("res", 100.0)).intValue();

        if (params.containsKey("expressions")) {
            scope.addInvocationVariable("u");
            scope.addInvocationVariable("v");
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
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public Location[] distribute() {
        List<Location> points = new LinkedList<>();

        double t = (System.currentTimeMillis() - startTime) / 1000.;
        for (int i = 0; i < res; i++) {
            double k = i + .5f;

            double u = Math.PI * (1 + Math.sqrt(5)) * k;
            double v = Math.acos(1f - 2f * k / res);

            Location location = evaluateExpressions(u,v,i,t);

            if (location != null)
                points.add(location);
        }

        Location[] array = new Location[points.size()];
        for (int i = 0; i < points.size(); i++)
            array[i] = points.get(i);

        return array;
    }

    private Location evaluateExpressions(double u, double v, double index, double time) {
        double x = Math.cos(u) * Math.sin(v);
        double y = Math.cos(v);
        double z = Math.sin(u) * Math.sin(v);

        double wx = x * region.r + region.x;
        double wy = y * region.r + region.y;
        double wz = z * region.r + region.z;

        if (expressions == null)
            return new Location(Bukkit.getWorld(region.worldName), wx, wy, wz);

        double l = 1;

        for (int i = 0; i < expressions.size(); i++) {
            switch (expressionTypes.get(i)) {
                case "mask":
                    if (expressions.get(i).evaluate(u, v, x, y, z, wx, wy, wz, index, time) <= 0)
                        return null;
                    break;
                case "map_u":
                    u = expressions.get(i).evaluate(u, v, x, y, z, wx, wy, wz, index, time);
                    x = Math.cos(u) * Math.sin(v) * l;
                    z = Math.sin(u) * Math.sin(v) * l;
                    break;
                case "map_v":
                    v = expressions.get(i).evaluate(u, v, x, y, z, wx, wy, wz, index, time);
                    x = Math.cos(u) * Math.sin(v) * l;
                    z = Math.sin(u) * Math.sin(v) * l;
                    y = Math.cos(v) * l;
                    break;
                case "map_l":
                    double ld = expressions.get(i).evaluate(u, v, x, y, z, wx, wy, wz, index, time) / l;
                    l *= ld;
                    x *= ld;
                    y *= ld;
                    z *= ld;
                    break;
                case "map_x":
                    x = expressions.get(i).evaluate(u, v, x, y, z, wx, wy, wz, index, time);
                    break;
                case "map_y":
                    y = expressions.get(i).evaluate(u, v, x, y, z, wx, wy, wz, index, time);
                    break;
                case "map_z":
                    z = expressions.get(i).evaluate(u, v, x, y, z, wx, wy, wz, index, time);
                    break;
                default:
                    return null;
            }
            wx = x * region.r + region.x;
            wy = y * region.r + region.y;
            wz = z * region.r + region.z;
        }
        return new Location(Bukkit.getWorld(region.worldName), wx, wy, wz);
    }

    public static void register() {
        Aurora.registerDistributorType("SphereFib", "Sphere", SphereDistributor.class);
    }
}
