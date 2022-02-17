package digital.murl.aurora.regions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import digital.murl.aurora.Aurora;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Regions {
    private static Map<String, Region> regions;
    private static File regionsFile;

    public static void load() throws IOException {
        if (!Aurora.plugin.getDataFolder().exists())
            throw new IllegalStateException("The aurora data folder doesn't exist");

        regionsFile = new File(Aurora.plugin.getDataFolder(), "regions.json");

        refresh();
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

    private static void refresh() throws IOException {
        regions = new HashMap<>();

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

            reader.close();
        } catch (IOException e) {
            Aurora.logger.warning("Couldn't load regions from disk: " + e.getMessage());
        } catch (Exception e) {
            Aurora.logger.warning("Something unexpected happened: " + e.getMessage());
        }

        saveRegions();
    }

    private static void saveRegions() {
        JsonArray json = new JsonArray();
        for (Region region : regions.values())
            json.add(region.createJsonObject());

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
