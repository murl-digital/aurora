package fyi.sorenneedscoffee.aurora.http;

import com.google.gson.JsonSyntaxException;
import fyi.sorenneedscoffee.aurora.Aurora;

import java.util.HashMap;
import java.util.Map;

public abstract class Endpoint {

    protected boolean isInvalid(String jsonInString, Object target) {
        try {
            Aurora.gson.fromJson(jsonInString, target.getClass());
            return false;
        } catch (JsonSyntaxException e) {
            return true;
        }
    }
}
