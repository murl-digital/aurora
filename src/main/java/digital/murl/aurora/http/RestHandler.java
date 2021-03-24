package digital.murl.aurora.http;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import digital.murl.aurora.Aurora;
import org.apache.commons.lang.exception.ExceptionUtils;

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
    try {
      for (Endpoint endpoint : endpoints) {
        if (endpoint.path.matcher(path).matches()) {
          writeResponse(exchange,
              endpoint.handle(
                  Iterators.toArray(splitter.split(path).iterator(), String.class),
                  new InputStreamReader(exchange.getRequestBody())
              )
          );
          return;
        }
      }
    } catch (Exception e) {
      Aurora.logger.warning(
              "An effect failed to execute. Please report the following information to Joe:");
      if (e.getMessage() != null) {
        Aurora.logger.warning(e.getMessage());
      }
      Aurora.logger.warning(ExceptionUtils.getStackTrace(e));

      writeResponse(exchange, Response.serverError().build());
      return;
    }

    exchange.sendResponseHeaders(404, 0);
    exchange.close();
  }

  private void writeResponse(HttpExchange exchange, Response response) throws IOException {
    if (response.hasEntity()) {
      exchange.sendResponseHeaders(response.getStatusCode(), response.getLength());
      String entity = response.getEntity();
      exchange.getResponseBody().write(entity.getBytes());
    } else {
      exchange.sendResponseHeaders(response.getStatusCode(), 0);
    }
    exchange.close();
  }
}
