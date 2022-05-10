package digital.murl.aurora.regions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import digital.murl.aurora.Plugin;
import digital.murl.aurora.regions.distributors.*;

import java.io.*;
import java.util.*;

public class RegionManager {
    private static Map<String, Region> regions;

    private static List<JsonObject> failedRegions;
    private static File regionsFile;

    public static void load() throws IOException {
        if (!Plugin.plugin.getDataFolder().exists())
            throw new IllegalStateException("The aurora data folder doesn't exist");

        regionsFile = new File(Plugin.plugin.getDataFolder(), "regions.json");

        refresh();
    }

    public static void save() {
        new Thread(RegionManager::saveRegions).start();
    }

    public static void addRegion(Region region) {
        if (!regions.keySet().contains(region.id))
            regions.put(region.id, region);
    }

    public static void removeRegion(String id) {
        if (regions.keySet().contains(id))
            regions.remove(id);
    }

    public static Region getRegion(String id) {
        if (regions.keySet().contains(id))
            return regions.get(id);
        return null;
    }

    public static List<Region> getRegions() {
        return Collections.unmodifiableList(new ArrayList<>(regions.values()));
    }

    public static void refresh() throws IOException {
        regions = new HashMap<>();
        failedRegions = new ArrayList<>();

        if (!regionsFile.exists()) {
            try {
                FileWriter writer = new FileWriter(regionsFile);
                writer.write("[]");
                writer.close();
            } catch (IOException e){
                Plugin.logger.warning("Couldn't create points file: " + e.getMessage());
            }
            return;
        }

        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(regionsFile));
            JsonArray json = gson.fromJson(reader, JsonArray.class);

            for (JsonElement element : json) {
                Map<String,Object> object = gson.fromJson(element, HashMap.class);
                String regionType = (String)object.get("RegionType");
                RegionMapConstructor constructor = RegionRegistrar.getMapConstructor(regionType.toLowerCase());
                if (constructor != null)
                    addRegion(constructor.regionConstructor(object));
                else {
                    Plugin.logger.warning(String.format("Failed to load region of type [%s], storing in background.", regionType));
                    failedRegions.add(element.getAsJsonObject());
                }
            }

            reader.close();

            saveRegions();
        } catch (IOException e) {
            Plugin.logger.warning("Couldn't load regions from disk: " + e.getMessage());
        } catch (Exception e) {
            Plugin.logger.warning("Something unexpected happened: " + e.getMessage());
        }
    }

    private static void saveRegions() {
        Gson gson = new Gson();
        JsonArray json = new JsonArray();
        for (Region region : regions.values()) {
            Map<String, Object> object = new HashMap<>();
            object.put("RegionType", region.type);
            object.put("id", region.id);
            object.put("world", region.worldName);
            region.populateMap(object);

            json.add(gson.toJsonTree(object));
        }
        for (JsonObject object : failedRegions)
            json.add(object);

        String jsonString = json.toString();

        try {
            FileWriter writer = new FileWriter(regionsFile);
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            Plugin.logger.warning("Couldn't save regions to disk: " + e.getMessage());
        }
    }
}
