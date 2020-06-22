package fyi.sorenneedscoffee.aurora.http;

import com.google.gson.JsonSyntaxException;
import fyi.sorenneedscoffee.aurora.Aurora;

import java.util.HashMap;
import java.util.Map;

public abstract class Endpoint {

    protected boolean isInvalid(String jsonInString) {
        try {
            Aurora.gson.fromJson(jsonInString, Object.class);
            return false;
        } catch (JsonSyntaxException e) {
            try {
                Aurora.gson.fromJson(jsonInString, Object[].class);
                return false;
            } catch (JsonSyntaxException f) {
                return true;
            }
        }
    }

    protected Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
