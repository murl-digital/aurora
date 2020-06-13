package fyi.sorenneedscoffee.eyecandy.http.endpoints;

import com.sun.net.httpserver.HttpExchange;
import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.implementations.DragonEffect;
import fyi.sorenneedscoffee.eyecandy.http.Endpoint;
import fyi.sorenneedscoffee.eyecandy.http.requests.DragonRequest;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;
import fyi.sorenneedscoffee.eyecandy.util.Point;

public class DragonEndpoint extends Endpoint {

    public DragonEndpoint() {
        super("POST");
    }

    @Override
    protected void process(HttpExchange httpExchange, String jsonBody) throws Exception {
        DragonRequest request;
        try {
            request = EyeCandy.gson.fromJson(jsonBody, DragonRequest.class);
        } catch (Exception e) {
            respond(httpExchange, 400);
            return;
        }

        Point point = EyeCandy.pointUtil.getPoint(request.pointId);
        DragonEffect effect = new DragonEffect();
        effect.isStatic = request.isStatic;

        EffectManager.doAction(request.action, effect, point);

        respond(httpExchange, 200);
    }
}
