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

public abstract class Endpoint implements HttpHandler {
    private final String method;

    public Endpoint(String method) {
        this.method = method;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (!checkPath(httpExchange.getRequestURI().getPath())) {
            respond(httpExchange, 404);
            return;
        }

        if (!method.equals(httpExchange.getRequestMethod())) {
            respond(httpExchange, 400);
            return;
        }

        try {
            byte[] target = new byte[httpExchange.getRequestBody().available()];
            httpExchange.getRequestBody().read(target);
            String in = new String(target);
            process(httpExchange, in);
        } catch (Exception e) {
            EyeCandy.logger.severe(e.getMessage());
            EyeCandy.logger.severe(ExceptionUtils.getStackTrace(e));
            respond(httpExchange, 500);
        }
    }

    protected void respond(HttpExchange httpExchange, int responseCode) throws IOException {
        httpExchange.sendResponseHeaders(responseCode, 0);
        httpExchange.getResponseBody().close();
    }

    protected abstract void process(HttpExchange httpExchange, String body) throws Exception;
    protected abstract boolean checkPath(String path);

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
