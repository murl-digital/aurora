package fyi.sorenneedscoffee.eyecandy.util;

import fyi.sorenneedscoffee.eyecandy.EyeCandy;
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
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private final EyeCandy plugin;
    File pointsFile;
    private FileConfiguration points;

    public DataManager(EyeCandy plugin) {
        this.plugin = plugin;
        initPointsFile();
    }

    public void initConfigFile() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            if (configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            plugin.saveResource("config.yml", false);
        }
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
        EyeCandy.pointUtil.refresh();
    }

    public int getAvailableId() {
        int result = 0;
        for (String key : points.getConfigurationSection("points").getKeys(false)) {
            if (key.equals(Integer.toString(result)))
                result++;
        }
        return result;
    }

    public void removePointFromFile(int id) {
        points.set("points." + id, null);
        EyeCandy.pointUtil.refresh();
    }

    public List<Point> loadPoints() {
        ConfigurationSection section = points.getConfigurationSection("points");
        if (section == null) {
            section = points.createSection("points");
        }
        List<Point> points = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            World world = Bukkit.getWorld(section.getConfigurationSection(key).getString("world"));
            Vector vector = section.getConfigurationSection(key).getVector("vector");
            Location loc = new Location(world, vector.getX(), vector.getY(), vector.getZ());

            points.add(new Point(Integer.parseInt(key), loc));
        }

        return points;
    }
}
