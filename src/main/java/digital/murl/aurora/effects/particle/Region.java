package digital.murl.aurora.effects.particle;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.points.Point;
import org.bukkit.Location;
import org.mariuszgromada.math.mxparser.Function;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

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

      Location loc1 = points[0].getLocation();
      Location loc2 = points[1].getLocation();

      double xMin = Math.min(loc1.getX(), loc2.getX());
      double xMax = Math.max(loc1.getX(), loc2.getX());
      double yMin = Math.min(loc1.getY(), loc2.getY());
      double yMax = Math.max(loc1.getY(), loc2.getY());
      double zMin = Math.min(loc1.getZ(), loc2.getZ());
      double zMax = Math.max(loc1.getZ(), loc2.getZ());

      // no 0-size regions for you!
      if (xMin == xMax) xMax++;
      if (yMin == yMax) yMax++;
      if (zMin == zMax) zMax++;

      long particleCount = Math.round(((xMax - xMin) * (yMax - yMin) * (zMax - zMin)) * (density * 0.001));
      Aurora.logger.info(Long.toString(particleCount));

      Iterator<Double> xRands = random.doubles(xMin, xMax).iterator();
      Iterator<Double> yRands = random.doubles(yMin, yMax).iterator();
      Iterator<Double> zRands = random.doubles(zMin, zMax).iterator();

      for (long i = 0; i < particleCount; i++) {
        result.add(new Location(loc1.getWorld(), xRands.next(), yRands.next(), zRands.next()));
      }

    } else if (type == RegionType.EQUATION) {
      Function function = new Function("f(x,y)=" + equation);
      Random random = new Random();
      generateCuboid((pos1, pos2, x, y, z, xMult, yMult, zMult, xDistance, zDistance) -> {
        /*if (randomized) {
          double yCalc = function.calculate(x - (xDistance / 2), z - (zDistance / 2));
          if (!Double.isNaN(yCalc)) {
            result.add(pos1.clone().add((x * xMult), (yCalc * yMult), (z * zMult)));
          }
        } else if (random.nextDouble() <= density) {
          double yCalc = function.calculate(x - (xDistance / 2), z - (zDistance / 2));
          if (!Double.isNaN(yCalc)) {
            result.add(pos1.clone().add((x * xMult), (yCalc * yMult), (z * zMult)));
          }
        }*/
        double yCalc = function.calculate(x - (xDistance / 2), z - (zDistance / 2));
        if (!Double.isNaN(yCalc)) {
          result.add(pos1.clone().add((x * xMult), (yCalc * yMult), (z * zMult)));
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
