package fyi.sorenneedscoffee.eyecandy.http.endpoints;

import com.sun.net.httpserver.HttpExchange;
import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.dragon.DragonEffect;
import fyi.sorenneedscoffee.eyecandy.effects.EffectGroup;
import fyi.sorenneedscoffee.eyecandy.http.Endpoint;
import fyi.sorenneedscoffee.eyecandy.http.requests.DragonModel;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;
import fyi.sorenneedscoffee.eyecandy.util.Point;

import java.util.UUID;

public class DragonEndpoint extends Endpoint {

    public DragonEndpoint() {
        super("POST");
    }

    @Override
    protected void process(HttpExchange httpExchange, String body) throws Exception {
        if (isInvalid(body)) {
            respond(httpExchange, 400);
            return;
        }

        UUID id = UUID.fromString(queryToMap(httpExchange.getRequestURI().getQuery()).get("id"));
        if(httpExchange.getRequestURI().getPath().contains("restart")) {
            EffectManager.restartEffect(id);
        } else {
            DragonModel[] request = EyeCandy.gson.fromJson(body, DragonModel[].class);

            EffectGroup group = new EffectGroup(id);
            for (DragonModel model : request) {
                Point point = EyeCandy.pointUtil.getPoint(model.pointId);
                DragonEffect effect = new DragonEffect(point, model.isStatic);
                group.add(effect);
            }

            if (httpExchange.getRequestURI().getPath().contains("start"))
                EffectManager.startEffect(group);
        }

        respond(httpExchange, 200);
    }

    @Override
    protected boolean checkPath(String path) {
        return path.contains("start") || path.contains("restart");
    }
}
