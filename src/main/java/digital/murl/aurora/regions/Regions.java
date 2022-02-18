package digital.murl.aurora.regions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import digital.murl.aurora.Aurora;

import java.io.*;
import java.util.*;

public class Regions {
    private static Map<String, Region> regions;
    private static Map<String, RegionJsonConstructor> constructors = new HashMap<>();
    private static List<JsonObject> failedRegions;
    private static File regionsFile;

    public static void load() throws IOException {
        if (!Aurora.plugin.getDataFolder().exists())
            throw new IllegalStateException("The aurora data folder doesn't exist");

        regionsFile = new File(Aurora.plugin.getDataFolder(), "regions.json");

        refresh();
    }

    public static void addRegionConstructor(String name, RegionJsonConstructor constructor) {
        constructors.put(name.toLowerCase(), constructor);
    }

    public static void save() {
        new Thread(Regions::saveRegions).start();
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
                writer.close();
            } catch (IOException e){
                Aurora.logger.warning("Couldn't create points file: " + e.getMessage());
            }
            return;
        }

        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(regionsFile));
            JsonArray json = gson.fromJson(reader, JsonArray.class);

            for (JsonElement element : json) {
                JsonObject object = element.getAsJsonObject();
                String regionType = object.get("RegionType").getAsString();
                if (constructors.containsKey(regionType.toLowerCase()))
                    addRegion(constructors.get(regionType.toLowerCase()).regionConstructor(object));
                else {
                    Aurora.logger.warning(String.format("Failed to load region of type [%s], storing in background.", regionType));
                    failedRegions.add(object);
                }
            }

            reader.close();

            saveRegions();
        } catch (IOException e) {
            Aurora.logger.warning("Couldn't load regions from disk: " + e.getMessage());
        } catch (Exception e) {
            Aurora.logger.warning("Something unexpected happened: " + e.getMessage());
        }
    }

    private static void saveRegions() {
        JsonArray json = new JsonArray();
        for (Region region : regions.values()) {
            JsonObject object = new JsonObject();
            object.addProperty("RegionType", region.type);
            object.addProperty("id", region.id);
            object.addProperty("world", region.worldName);
            region.populateJsonObject(object);
            json.add(object);
        }
        for (JsonObject object : failedRegions)
            json.add(object);

        String jsonString = json.toString();

        try {
            FileWriter writer = new FileWriter(regionsFile);
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            Aurora.logger.warning("Couldn't save regions to disk: " + e.getMessage());
        }
    }
}
