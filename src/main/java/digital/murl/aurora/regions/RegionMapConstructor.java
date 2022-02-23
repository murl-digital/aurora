package digital.murl.aurora.regions;

import java.util.Map;

@FunctionalInterface
public interface RegionMapConstructor {
    Region regionConstructor(Map<String, Object> data);
}
