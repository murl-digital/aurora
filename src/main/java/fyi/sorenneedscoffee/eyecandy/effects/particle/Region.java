package fyi.sorenneedscoffee.eyecandy.effects.particle;

import fyi.sorenneedscoffee.eyecandy.util.Point;
import org.bukkit.Location;
import org.mariuszgromada.math.mxparser.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Region {
    private final Point[] points;
    public final RegionType type;
    public final double density;
    public final boolean randomized;
    private final String equation;

    //The lower the value, the more particles per block. DO NOT PUT THIS BELOW 1.0!!!!
    private final double resolution = 0.5;

    public Region(Point[] points, RegionType type, double density, boolean randomized, String equation) {
        this.points = points;
        this.type = type;
        this.density = density;
        this.randomized = randomized;
        this.equation = equation;
    }

    public List<Location> calculateLocations() {
        List<Location> result = new ArrayList<>();
        if(type == RegionType.POINTS) {
            for (Point point : points) {
                result.add(point.getLocation());
            }
        } else if (type == RegionType.CUBOID) {
            Random random = new Random();
//            Location pos1 = points[0].getLocation();
//            Location pos2 = points[1].getLocation();
//            int xMult = pos1.getX() < pos2.getX() ? 1 : -1;
//            int yMult = pos1.getY() < pos2.getY() ? 1 : -1;
//            int zMult = pos1.getZ() < pos2.getZ() ? 1 : -1;
//            double zDistance = Math.abs(pos1.getZ() - pos2.getZ());
//            double xDistance = Math.abs(pos1.getX() - pos2.getX());
//            for(double y = 0; pos1.getY() + y < pos2.getY(); y += resolution) {
//                for(double z = 0; z <= zDistance; z += resolution) {
//                    for(double x = 0; x <= xDistance; x += resolution) {
//                        if (randomized)
//                            result.add(pos1.clone().add((x*xMult), (y*yMult), (z*zMult)));
//                        else if (random.nextDouble() <= density)
//                            result.add(pos1.clone().add((x*xMult), (y*yMult), (z*zMult)));
//                    }
//                }
//            }
            generateCuboid((pos1, pos2, x, y, z, xMult, yMult, zMult, a, b) -> {
                if (randomized)
                    result.add(pos1.clone().add((x*xMult), (y*yMult), (z*zMult)));
                else if (random.nextDouble() <= density)
                    result.add(pos1.clone().add((x*xMult), (y*yMult), (z*zMult)));
            });
        } else if (type == RegionType.EQUATION) {
            Function function = new Function("f(x,y)="+equation);
            Random random = new Random();
//            Location pos1 = points[0].getLocation();
//            Location pos2 = points[1].getLocation();
//            int xMult = pos1.getX() < pos2.getX() ? 1 : -1;
//            int yMult = pos1.getY() < pos2.getY() ? 1 : -1;
//            int zMult = pos1.getZ() < pos2.getZ() ? 1 : -1;
//            double zDistance = Math.abs(pos1.getZ() - pos2.getZ());
//            double xDistance = Math.abs(pos1.getX() - pos2.getX());
//            for(double y = 0; pos1.getY() + y < pos2.getY(); y += resolution) {
//                for(double z = 0; z <= zDistance; z += resolution) {
//                    for(double x = 0; x <= xDistance; x += resolution) {
//                        if (randomized) {
//                            double yCalc = function.calculate(x - (xDistance / 2), z - (zDistance / 2));
//                            if(!Double.isNaN(yCalc))
//                                result.add(pos1.clone().add((x * xMult), (yCalc * yMult), (z * zMult)));
//                        } else if (random.nextDouble() <= density) {
//                            double yCalc = function.calculate(x - (xDistance / 2), z - (zDistance / 2));
//                            if(!Double.isNaN(yCalc))
//                                result.add(pos1.clone().add((x * xMult), (yCalc * yMult), (z * zMult)));
//                        }
//                    }
//                }
//            }
            generateCuboid((pos1, pos2, x, y, z, xMult, yMult, zMult, xDistance, zDistance) -> {
                if (randomized) {
                    double yCalc = function.calculate(x - (xDistance / 2), z - (zDistance / 2));
                    if(!Double.isNaN(yCalc))
                        result.add(pos1.clone().add((x * xMult), (yCalc * yMult), (z * zMult)));
                } else if (random.nextDouble() <= density) {
                    double yCalc = function.calculate(x - (xDistance / 2), z - (zDistance / 2));
                    if(!Double.isNaN(yCalc))
                        result.add(pos1.clone().add((x * xMult), (yCalc * yMult), (z * zMult)));
                }
            });
        }
        return result;
    }

    private void generateCuboid(LogicalFunction function) {
        Location pos1 = points[0].getLocation();
        Location pos2 = points[1].getLocation();
        int xMult = pos1.getX() < pos2.getX() ? 1 : -1;
        int yMult = pos1.getY() < pos2.getY() ? 1 : -1;
        int zMult = pos1.getZ() < pos2.getZ() ? 1 : -1;
        double zDistance = Math.abs(pos1.getZ() - pos2.getZ());
        double xDistance = Math.abs(pos1.getX() - pos2.getX());
        for(double y = 0; pos1.getY() + y < pos2.getY(); y += resolution) {
            for (double z = 0; z <= zDistance; z += resolution) {
                for (double x = 0; x <= xDistance; x += resolution) {
                    function.execute(
                            pos1,
                            pos2,
                            x,
                            y,
                            z,
                            xMult,
                            yMult,
                            zMult,
                            xDistance,
                            zDistance
                    );
                }
            }
        }
    }

    @FunctionalInterface
    private interface LogicalFunction {
        void execute(Location pos1, Location pos2, double x, double y, double z, double xMult, double yMult, double zMult, double xDistance, double zDistance);
    }
}
