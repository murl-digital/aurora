package fyi.sorenneedscoffee.aurora.http;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class RestHandler implements HttpHandler {
    private final Set<Endpoint> endpoints = new HashSet<>();
    private final Splitter splitter = Splitter.on('/').omitEmptyStrings();

    public void register(Endpoint endpoint) {
        endpoints.add(endpoint);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        for (Endpoint endpoint : endpoints) {
            if (endpoint.path.matcher(path).matches()) {
                writeResponse(exchange,
                        endpoint.handle(
                                Iterators.toArray(splitter.split(path).iterator(), String.class),
                                new InputStreamReader(exchange.getRequestBody())
                        )
                );
                break;
            }
        }

        exchange.sendResponseHeaders(404, 0);
        exchange.getResponseBody().close();
    }

    private void writeResponse(HttpExchange exchange, Response response) throws IOException {
        if (response.hasEntity()) {
            exchange.sendResponseHeaders(response.getStatusCode(), response.getLength());
            String entity = (String) response.getEntity();
            exchange.getResponseBody().write(entity.getBytes());
        } else {
            exchange.sendResponseHeaders(response.getStatusCode(), 0);
        }
        exchange.getResponseBody();
        exchange.close();
    }
}
