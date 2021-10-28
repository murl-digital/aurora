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
  public final long quantity;
  public final boolean randomized;

  private final Point[] points;
  private final String equation;

  //The lower the value, the more particles per block.
  private final double RESOLUTION = 0.8;

  public Region(Point[] points, RegionType type, double density, boolean randomized, String equation, long quantity) {
    this.points = points;
    this.type = type;
    this.density = density;
    this.randomized = randomized;
    this.equation = equation;
    this.quantity = quantity;
  }

  public Set<Location> calculateLocations() {
    Set<Location> result = new HashSet<>();
    if (type == RegionType.POINTS) {
      for (Point point : points) {
        result.add(point.getLocation());
      }
    } else if (type == RegionType.CUBOID) {
      this.generateCuboidPoints(result, false);

    } else if (type == RegionType.CUBOID_QUANTITATIVE) {
      this.generateCuboidPoints(result, true);

    } else if (type == RegionType.CYLINDER) {
      this.generateCylinderPoints(result);

    } else if (type == RegionType.SPHERE) {
      this.generateSpherePoints(result);

    } else if (type == RegionType.EQUATION) {
      Function function = new Function("f(x,y)=" + equation);
//      Random random = new Random();
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
    } else {
      Aurora.logger.warning("Unknown region type! Received: " + type);
    }
    return result;
  }

  private void generateSpherePoints(Set<Location> result) {
    // Don't explode on user error
    if (points.length != 2) {
      Aurora.logger.warning("Tried to generate points in sphere but incorrect number of points received. Expected=2, Actual="+points.length);
      return;
    }

    Random random = new Random();

    Location loc1 = points[0].getLocation();  // center point
    Location loc2 = points[1].getLocation();  // radius

    // 3d radius
    double radius = Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2) + Math.pow(loc1.getZ() - loc2.getZ(), 2) + Math.pow(loc1.getY() - loc2.getY(), 2));

    Iterator<Double> uRands = random.doubles(0, 1).iterator();
    Iterator<Double> vRands = random.doubles(0, 1).iterator();
    Iterator<Double> rRands = random.doubles(0, 1).iterator();

    double u, v, theta, phi, r, sinTheta, cosTheta, sinPhi, cosPhi;

    for (long i = 0; i < this.quantity; i++) {
      u = uRands.next();
      v = vRands.next();
      theta = u * 2.0 * Math.PI;
      phi = Math.acos(2.0 * v - 1.0);
      r = Math.cbrt(rRands.next()) * radius;
      sinTheta = Math.sin(theta);
      cosTheta = Math.cos(theta);
      sinPhi = Math.sin(phi);
      cosPhi = Math.cos(phi);

      result.add(new Location(loc1.getWorld(), (r * sinPhi * cosTheta) + loc1.getX(), (r * cosPhi) + loc1.getY(), (r * sinPhi * sinTheta) + loc1.getZ()));
    }

  }

  private void generateCylinderPoints(Set<Location> result) {
    // Don't explode on user error
    if (points.length != 2) {
      Aurora.logger.warning("Tried to generate points in cylinder but incorrect number of points received. Expected=2, Actual="+points.length);
      return;
    }

    Random random = new Random();

    Location loc1 = points[0].getLocation();  // center + top/bottom
    Location loc2 = points[1].getLocation();  // radius + top/bottom

    double yMin = Math.min(loc1.getY(), loc2.getY());
    double yMax = Math.max(loc1.getY(), loc2.getY());

    // no 0-size regions for you!
    if (yMin == yMax) yMax++;

    double radius = Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2) + Math.pow(loc1.getZ() - loc2.getZ(), 2));


    Iterator<Double> sRands = random.doubles(0, 1).iterator();
    Iterator<Double> thetaRands = random.doubles(0, 2*Math.PI).iterator();
    Iterator<Double> yRands = random.doubles(yMin, yMax).iterator();
    double r, theta;

    for (long i = 0; i < this.quantity; i++) {
      r = Math.sqrt(sRands.next()) * radius;
      theta = thetaRands.next();
      result.add(new Location(loc1.getWorld(), r * Math.cos(theta) + loc1.getX(), yRands.next(), r * Math.sin(theta) + loc1.getZ()));
    }

  }

  /**
   * Generates randomized points within a cuboid using a density percentage or a quantitative value
   * @param result - Where to stick the generated points
   * @param isQuantitative - Whether to use a quantitative number of points (true) or a % density (false)
   */
  private void generateCuboidPoints(Set<Location> result, boolean isQuantitative) {

    // Don't explode on user error
    if (points.length != 2) {
      Aurora.logger.warning("Tried to generate points in cuboid but incorrect number of points received. Expected=2, Actual="+points.length);
      return;
    }

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

    Iterator<Double> xRands = random.doubles(xMin, xMax).iterator();
    Iterator<Double> yRands = random.doubles(yMin, yMax).iterator();
    Iterator<Double> zRands = random.doubles(zMin, zMax).iterator();

    long count = this.quantity;
    if (!isQuantitative) {
      count = Math.round(((xMax - xMin) * (yMax - yMin) * (zMax - zMin)) * map(density, 0.00001, 0.01, 0, 1));
      Aurora.logger.info("Cuboid density computed to count: " + count);
    }

    for (long i = 0; i < count; i++) {
      result.add(new Location(loc1.getWorld(), xRands.next(), yRands.next(), zRands.next()));
    }
  }

  private void generateCuboid(LogicalFunction function) {
    // Don't explode on user error
    if (points.length != 2) {
      Aurora.logger.warning("Tried to generate points in equation cuboid but incorrect number of points received. Expected=2, Actual="+points.length);
      return;
    }

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

  private double map(double input, double input_start, double input_end, double output_start, double output_end) {
    return output_start + ((output_end - output_start) / (input_end - input_start)) * (input - input_start);
  }

  @FunctionalInterface
  private interface LogicalFunction {

    void execute(Location pos1, Location pos2, double x, double y, double z, double xMult,
        double yMult, double zMult, double xDistance, double zDistance);
  }
}
