package digital.murl.aurora.regions;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface RegionJsonConstructor {
    Region regionConstructor(JsonObject object);
}
