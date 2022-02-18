package digital.murl.aurora.regions;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface RegionConstructor<H extends JsonObject> {
    Region regionConstructor(H object);
}
