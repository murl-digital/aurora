package fyi.sorenneedscoffee.eyecandy.util;

import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
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
    private FileConfiguration points;
    File pointsFile;

    public DataManager(EyeCandy plugin) {
        this.plugin = plugin;
        initPointsFile();
    }

    private void initPointsFile() {
        pointsFile = new File(plugin.getDataFolder(), "points.yml");
        if(!pointsFile.exists()) {
            pointsFile.getParentFile().mkdirs();
            plugin.saveResource("points.yml", false);
        }
        points = new YamlConfiguration();
        try {
            points.load(pointsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        if(points.getConfigurationSection("points") == null) {
            points.createSection("points");
        }
    }

    public void addPointToFile(Point point) {
        ConfigurationSection section = points.getConfigurationSection("points");
        if(section == null) {
            section = points.createSection("points");
        }
        section.set(point.id+".world", point.location.getWorld().getName());
        section.set(point.id+".vector", point.location.toVector());
        try {
            points.save(pointsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removePointFromFile(Point point) {

    }

    public List<Point> loadPoints() {
        ConfigurationSection section = points.getConfigurationSection("points");
        if(section == null) {
            section = points.createSection("points");
        }
        List<Point> points = new ArrayList<>();
        for(String key : section.getKeys(false)) {
            World world = Bukkit.getWorld(section.getConfigurationSection(key).getString("world"));
            Vector vector = section.getConfigurationSection(key).getVector("vector");
            Location loc = new Location(world, vector.getX(), vector.getY(), vector.getZ());

            points.add(new Point(Integer.parseInt(key), loc));
        }

        return points;
    }
}
