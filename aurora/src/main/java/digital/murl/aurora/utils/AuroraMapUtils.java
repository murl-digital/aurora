package digital.murl.aurora.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.Map;

public class AuroraMapUtils {

//    public static Object[] getObjectArray(Map<String, Object> params, String key, Object[] defaultValue) {
//        if (!params.containsKey(key)) return defaultValue;
//
//        Object value = params.get(key);
//        if (value.getClass().isArray()) return (Object[])value;
//
//        return defaultValue;
//    }

    public static <T> T mapToObject(Map<String, Object> map, Type targetClass) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(map);
        return gson.fromJson(jsonElement, targetClass);
    }
}
