package fyi.sorenneedscoffee.aurora.http;

import com.google.gson.JsonSyntaxException;
import fyi.sorenneedscoffee.aurora.Aurora;

import java.util.HashMap;
import java.util.Map;

public abstract class Endpoint {

    protected <T> boolean isInvalid(String jsonInString, Class<T> target) {
        try {
            Aurora.gson.fromJson(jsonInString, target);
            return false;
        } catch (JsonSyntaxException e) {
            return true;
        }
    }
}
