package fyi.sorenneedscoffee.eyecandy.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.HashMap;

public class RestHandler implements HttpHandler {
    private final HashMap<String, HttpHandler> endpoints;

    public RestHandler() {
        this.endpoints = new HashMap<>();
    }

    public void register(String path, HttpHandler handler) {
        endpoints.put(path, handler);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (endpoints.containsKey(path)) {
            endpoints.get(path).handle(httpExchange);
        } else {
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.getResponseBody().close();
        }
    }
}
