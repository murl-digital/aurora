package digital.murl.aurora.regions;

import java.util.Map;

@FunctionalInterface
public interface RegionJsonConstructor {
    Region regionConstructor(Map<String, Object> data);
}
