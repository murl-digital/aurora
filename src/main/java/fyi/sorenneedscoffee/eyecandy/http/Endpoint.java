package fyi.sorenneedscoffee.eyecandy.http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Endpoint {

    protected boolean isInvalid(String jsonInString) {
        try {
            EyeCandy.gson.fromJson(jsonInString, Object.class);
            return false;
        } catch (JsonSyntaxException e) {
            try {
                EyeCandy.gson.fromJson(jsonInString, Object[].class);
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
