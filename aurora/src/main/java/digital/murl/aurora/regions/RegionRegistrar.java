package digital.murl.aurora.regions;

import digital.murl.aurora.regions.distributors.Distributor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class RegionRegistrar {
    private static final Map<String, RegionMapConstructor> mapConstructors = new HashMap<>();
    private static final Map<String, RegionParameterConstructor> parameterConstructors = new HashMap<>();
    private static final Map<String, RegionParameterCompleter> parameterCompleters = new HashMap<>();

    private static final Map<String, Class<? extends Distributor>> distributors = new HashMap<>();
    private static final Map<String, List<String>> regionDistributors = new HashMap<>();

    public static void addMapConstructor(String type, RegionMapConstructor constructor) {
        mapConstructors.put(type.toLowerCase(), constructor);
    }

    public static void addParameterConstructor(String type, RegionParameterConstructor constructor) {
        parameterConstructors.put(type.toLowerCase(), constructor);
    }

    public static void addParameterCompleter(String type, RegionParameterCompleter completer) {
        parameterCompleters.put(type.toLowerCase(), completer);
    }

    public static RegionMapConstructor getMapConstructor(String type) {
        return mapConstructors.containsKey(type) ? mapConstructors.get(type) : null;
    }

    public static RegionParameterConstructor getParameterConstructor(String type) {
        return parameterConstructors.containsKey(type) ? parameterConstructors.get(type) : null;
    }

    public static RegionParameterCompleter getParameterCompleter(String type) {
        return parameterCompleters.containsKey(type) ? parameterCompleters.get(type) : null;
    }

    public static Set<String> getRegionTypes() {
        return parameterConstructors.keySet();
    }

    public static void addRegionDistributor(String type, String regionType, Class<? extends Distributor> distributor) {
        distributors.put(type, distributor);
        if (regionDistributors.containsKey(regionType)) regionDistributors.get(regionType).add(type);
        else {
            List<String> list = new LinkedList<>();
            list.add(type);
            regionDistributors.put(regionType, list);
        }
    }

    public static Distributor getRegionDistributor(String type, Region region, Map<String, Object> params) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return distributors.containsKey(type) ? distributors.get(type).getDeclaredConstructor(Region.class, Map.class).newInstance(region, params) : null;
    }

    public static Set<String> getRegionDistributors() {
        return distributors.keySet();
    }

    public static List<String> getRegionDistributors(String regionType) {
        return regionDistributors.containsKey(regionType) ? regionDistributors.get(regionType) : null;
    }
}
