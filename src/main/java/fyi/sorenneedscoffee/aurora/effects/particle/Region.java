package fyi.sorenneedscoffee.aurora.effects.particle;

import fyi.sorenneedscoffee.aurora.points.Point;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.bukkit.Location;
import org.mariuszgromada.math.mxparser.Function;

public class Region {

  public final RegionType type;
  public final double density;
  public final boolean randomized;

  private final Point[] points;
  private final String equation;

  //The lower the value, the more particles per block.
  private final double RESOLUTION = 0.8;

  public Region(Point[] points, RegionType type, double density, boolean randomized,
      String equation) {
    this.points = points;
    this.type = type;
    this.density = density;
    this.randomized = randomized;
    this.equation = equation;
  }

  public Set<Location> calculateLocations() {
    Set<Location> result = new HashSet<>();
    if (type == RegionType.POINTS) {
      for (Point point : points) {
        result.add(point.getLocation());
      }
    } else if (type == RegionType.CUBOID) {
      Random random = new Random();
      generateCuboid((pos1, pos2, x, y, z, xMult, yMult, zMult, a, b) -> {
        if (randomized) {
          result.add(pos1.clone().add((x * xMult), (y * yMult), (z * zMult)));
        } else if (random.nextDouble() <= density) {
          result.add(pos1.clone().add((x * xMult), (y * yMult), (z * zMult)));
        }
      });
    } else if (type == RegionType.EQUATION) {
      Function function = new Function("f(x,y)=" + equation);
      Random random = new Random();
      generateCuboid((pos1, pos2, x, y, z, xMult, yMult, zMult, xDistance, zDistance) -> {
        if (randomized) {
          double yCalc = function.calculate(x - (xDistance / 2), z - (zDistance / 2));
          if (!Double.isNaN(yCalc)) {
            result.add(pos1.clone().add((x * xMult), (yCalc * yMult), (z * zMult)));
          }
        } else if (random.nextDouble() <= density) {
          double yCalc = function.calculate(x - (xDistance / 2), z - (zDistance / 2));
          if (!Double.isNaN(yCalc)) {
            result.add(pos1.clone().add((x * xMult), (yCalc * yMult), (z * zMult)));
          }
        }
      });
    }
    return result;
  }

  private void generateCuboid(LogicalFunction function) {
    Location pos1 =
        points[0].getLocation().getY() < points[1].getLocation().getY() ? points[0].getLocation()
            : points[1].getLocation();
    Location pos2 =
        points[1].getLocation().getY() > points[0].getLocation().getY() ? points[1].getLocation()
            : points[0].getLocation();
    int xMult = pos1.getX() < pos2.getX() ? 1 : -1;
    int yMult = pos1.getY() < pos2.getY() ? 1 : -1;
    int zMult = pos1.getZ() < pos2.getZ() ? 1 : -1;
    double xDistance = Math.abs(pos1.getX() - pos2.getX());
    double yDistance = Math.abs(pos1.getY() - pos2.getY());
    double zDistance = Math.abs(pos1.getZ() - pos2.getZ());
    for (double y = 0; y <= yDistance; y += RESOLUTION) {
      for (double z = 0; z <= zDistance; z += RESOLUTION) {
        for (double x = 0; x <= xDistance; x += RESOLUTION) {
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

    void execute(Location pos1, Location pos2, double x, double y, double z, double xMult,
        double yMult, double zMult, double xDistance, double zDistance);
  }
}
