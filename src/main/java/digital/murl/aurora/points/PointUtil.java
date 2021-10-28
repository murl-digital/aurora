package digital.murl.aurora.points;

import digital.murl.aurora.Aurora;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.TreeSet;

public class PointUtil {

  private TreeSet<Point> points;
  private final ArrayList<Hologram> holograms = new ArrayList<>();
  private String hologramPrefix;
  private String hologramSuffix;

  public PointUtil load() {
    points = Aurora.dataContext.loadPoints();
    //Aurora.dataContext.savePoints(points);
    return this;
  }

  public int getAvailableId() {
    int result = points.size();
      while (points.contains(new Point(result, null))) {
          result++;
      }
    return result;
  }

  public void refresh() {
    points = Aurora.dataContext.loadPoints();
    // If holograms are enabled (low-tech check) then add one for this new point too!
    if (holograms.size() > 0) {
      this.showHolograms(hologramPrefix, hologramSuffix);
    }
  }

  public Point getPoint(int id) {
    return points.stream().filter(p -> p.id == id).findAny().orElse(null);
  }

  public TreeSet<Point> getPoints() {
    return (TreeSet<Point>) points.clone();
  }

  public void savePoint(Point point) {
    points.add(point);
    Aurora.dataContext.savePoints(points);

    // If holograms are enabled (low-tech check) then add one for this new point too!
    if (holograms.size() > 0) {
      this.addHologram(point, hologramPrefix, hologramSuffix);
    }
  }

  public void showHolograms() {
    this.showHolograms(Aurora.config.getString("holograms.prefix", "§a"), Aurora.config.getString("holograms.suffix", ""));
  }

  public void showHolograms(String prefix) {
    this.showHolograms(prefix, Aurora.config.getString("holograms.suffix", ""));
  }

  public void showHolograms(String prefix, String suffix) {
    // Ensure HD is loaded before continuing
    if (!Aurora.plugin.isHolographicDisplaysLoaded()) {
      Aurora.logger.info("Not showing point holograms... Holographic Displays plugin is not loaded :(");
      return;
    }

    // Hide everything if already showing
    this.hideHolograms();

    // Update the last used modifiers
    hologramPrefix = prefix;
    hologramSuffix = suffix;

    // Make a hologram for each registered point
    for (Point point : this.getPoints()) {
      this.addHologram(point, hologramPrefix, hologramSuffix);
    }
    Aurora.logger.info("Point holograms have been added using prefix");
  }

  private void addHologram(Point point, String prefix, String suffix) {
    Hologram hologram = HologramsAPI.createHologram(Aurora.plugin, point.getLocation().clone().add(new Vector(0, .25, 0)));
    hologram.appendTextLine(prefix + point.id + suffix);
    hologram.getVisibilityManager().setVisibleByDefault(false);

    // Show the hologram only to admins
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.hasPermission("aurora.admin")) {
        hologram.getVisibilityManager().showTo(player);
//        Aurora.logger.info("Showing point holograms to " + player.getName());
      }
    }
    holograms.add(hologram);
  }

  public void hideHolograms() {
    // Ensure HD is loaded before continuing
    if (!Aurora.plugin.isHolographicDisplaysLoaded()) {
      Aurora.logger.info("Not hiding point holograms... Holographic Displays plugin is not loaded :(");
      return;
    }

    // Purge each registered hologram
    for (Hologram hologram : holograms) {
      hologram.delete();
    }

    // Cleanup
    holograms.clear();
    Aurora.logger.info("Point holograms have been removed");
  }

}
