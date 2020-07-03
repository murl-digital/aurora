package fyi.sorenneedscoffee.aurora.util;

import fyi.sorenneedscoffee.aurora.Aurora;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataManager {
    private final Aurora plugin;
    File pointsFile;
    private FileConfiguration points;

    public DataManager(Aurora plugin) {
        this.plugin = plugin;
        initPointsFile();
    }

    private void initPointsFile() {
        pointsFile = new File(plugin.getDataFolder(), "points.yml");
        if (!pointsFile.exists()) {
            pointsFile.getParentFile().mkdirs();
            plugin.saveResource("points.yml", false);
        }
        points = new YamlConfiguration();
        try {
            points.load(pointsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        if (points.getConfigurationSection("points") == null) {
            points.createSection("points");
        }
    }

    public void addPointToFile(Point point) {
        ConfigurationSection section = points.getConfigurationSection("points");
        if (section == null) {
            section = points.createSection("points");
        }
        Location loc = point.getLocation();
        section.set(point.id + ".world", loc.getWorld().getName());
        section.set(point.id + ".vector", loc.toVector());
        try {
            points.save(pointsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePoints(Set<Point> set) {
        points.set("points", null);
        for (Point point : set) {
            addPointToFile(point);
        }
    }

    public void removePointFromFile(int id) {
        points.set("points." + id, null);
        Aurora.pointUtil.refresh();
    }

    public TreeSet<Point> loadPoints() {
        try {
            points.load(pointsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        ConfigurationSection section = points.getConfigurationSection("points");
        if (section == null) {
            section = points.createSection("points");
        }
        TreeSet<Point> points = new TreeSet<>();
        for (String key : section.getKeys(false)) {
            World world = Bukkit.getWorld(section.getConfigurationSection(key).getString("world"));
            Vector vector = section.getConfigurationSection(key).getVector("vector");
            Location loc = new Location(world, vector.getX(), vector.getY(), vector.getZ());

            points.add(new Point(Integer.parseInt(key), loc));
        }

        return points;
    }
}
