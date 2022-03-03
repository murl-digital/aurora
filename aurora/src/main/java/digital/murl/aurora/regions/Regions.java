package digital.murl.aurora.regions;

import digital.murl.aurora.regions.distributors.RegionDistributor;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Regions {
    public static void addMapConstructor(String type, RegionMapConstructor constructor) {
        RegionManager.addMapConstructor(type, constructor);
    }

    public static void addParameterConstructor(String type, RegionParameterConstructor constructor) {
        RegionManager.addParameterConstructor(type, constructor);
    }

    public static void addParameterCompleter(String type, RegionParameterCompleter completer) {
        RegionManager.addParameterCompleter(type, completer);
    }

    public static void addRegionDistributor(String type, RegionDistributor distributor) {
        RegionManager.addRegionDistributor(type, distributor);
    }

    public static RegionParameterConstructor getParameterConstructor(String type) {
        return RegionManager.getParameterConstructor(type);
    }

    public static RegionParameterCompleter getParameterCompleter(String type) {
        return RegionManager.getParameterCompleter(type);
    }

    public static RegionDistributor getRegionDistributor(String type) {
        return RegionManager.getRegionDistributor(type);
    }

    public static Set<String> getRegionTypes() {
        return RegionManager.getRegionTypes();
    }

    public static Set<String> getRegionDistributors() {
        return RegionManager.getRegionDistributors();
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
