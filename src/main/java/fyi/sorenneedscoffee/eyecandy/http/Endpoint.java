package fyi.sorenneedscoffee.eyecandy.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.IOException;

public abstract class Endpoint implements HttpHandler {
    private final String method;

    public Endpoint(String method) {
        this.method = method;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (!method.equals(httpExchange.getRequestMethod())) {
            respond(httpExchange, 400);
            return;
        }

        try {
            byte[] target = new byte[httpExchange.getRequestBody().available()];
            httpExchange.getRequestBody().read(target);
            String in = new String(target);
            if (isJSONValid(in)) {
                process(httpExchange, in);
            } else {
                respond(httpExchange, 400);
            }
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

    protected abstract void process(HttpExchange httpExchange, String jsonBody) throws Exception;

    private boolean isJSONValid(String jsonInString) {
        try {
            EyeCandy.gson.fromJson(jsonInString, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }
}
