package fyi.sorenneedscoffee.eyecandy.http.endpoints;

import com.sun.net.httpserver.HttpExchange;
import fyi.sorenneedscoffee.eyecandy.http.Endpoint;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;

import java.util.UUID;

public class StopEndpoint extends Endpoint {

    public StopEndpoint() {
        super("POST");
    }

    @Override
    protected void process(HttpExchange httpExchange, String body) throws Exception {
        String sId = queryToMap(httpExchange.getRequestURI().getQuery()).get("id");
        if("all".equals(sId)) {
            EffectManager.stopAll();
        } else {
            UUID id = UUID.fromString(sId);
            EffectManager.stopEffect(id);
        }

        respond(httpExchange, 200);
    }

    @Override
    protected boolean checkPath(String path) {
        return path.equals("/effects/stop");
    }
}
