package fyi.sorenneedscoffee.aurora.http;

import com.google.gson.JsonSyntaxException;
import fyi.sorenneedscoffee.aurora.Aurora;

import java.io.Reader;

public abstract class Endpoint {

    protected <T> boolean isInvalid(Reader reader, Class<T> target) {
        try {
            Aurora.gson.fromJson(reader, target);
            return false;
        } catch (JsonSyntaxException e) {
            return true;
        }
    }
}
