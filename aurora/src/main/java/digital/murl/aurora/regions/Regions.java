package digital.murl.aurora.regions;

import digital.murl.aurora.Plugin;
import digital.murl.aurora.regions.distributors.Distributor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Regions {
    public static void addMapConstructor(String type, RegionMapConstructor constructor) {
        RegionRegistrar.addMapConstructor(type, constructor);
    }

    public static void addParameterConstructor(String type, RegionParameterConstructor constructor) {
        RegionRegistrar.addParameterConstructor(type, constructor);
    }

    public static void addParameterCompleter(String type, RegionParameterCompleter completer) {
        RegionRegistrar.addParameterCompleter(type, completer);
    }

    public static RegionParameterConstructor getParameterConstructor(String type) {
        return RegionRegistrar.getParameterConstructor(type);
    }

    public static RegionParameterCompleter getParameterCompleter(String type) {
        return RegionRegistrar.getParameterCompleter(type);
    }

    public static Set<String> getRegionTypes() {
        return RegionRegistrar.getRegionTypes();
    }

    public static void addRegion(Region region) {
        RegionManager.addRegion(region);
    }

    public static void removeRegion(String id) {
        RegionManager.removeRegion(id);
    }

    public static Region getRegion(String id) {
        return RegionManager.getRegion(id);
    }

    public static Distributor getDistributor(String type, Region region, Map<String, Object> params) {
        try {
            return RegionRegistrar.getRegionDistributor(type, region, params);
        } catch (Exception e) {
            Plugin.logger.warning("Couldn't create distributor: " + e.getMessage());
            return null;
        }
    }

    public static List<Region> getRegions() {
        return RegionManager.getRegions();
    }

    public static void save() {
        RegionManager.save();
    }

    public static void load() throws IOException {
        RegionManager.load();
    }

    public static void refresh() throws IOException {
        RegionManager.refresh();
    }
}
