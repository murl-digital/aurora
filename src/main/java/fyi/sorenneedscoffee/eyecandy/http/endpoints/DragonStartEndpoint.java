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

public class DragonStartEndpoint extends Endpoint {

    public DragonStartEndpoint() {
        super("POST");
    }

    @Override
    protected void process(HttpExchange httpExchange, String body) throws Exception {
        if (isInvalid(body)) {
            respond(httpExchange, 400);
            return;
        }

        UUID id = UUID.fromString(queryToMap(httpExchange.getRequestURI().getQuery()).get("id"));
        DragonModel[] request = EyeCandy.gson.fromJson(body, DragonModel[].class);

        EffectGroup effectGroup = new EffectGroup(id);
        for (DragonModel model : request) {
            Point point = EyeCandy.pointUtil.getPoint(model.pointId);
            DragonEffect effect = new DragonEffect(point, model.isStatic);
            effectGroup.add(effect);
        }

        EffectManager.startEffect(effectGroup);

        respond(httpExchange, 200);
    }
}
