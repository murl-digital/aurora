package fyi.sorenneedscoffee.eyecandy.http.endpoints;

import com.sun.net.httpserver.HttpExchange;
import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.EffectGroup;
import fyi.sorenneedscoffee.eyecandy.effects.time.TimeShiftEffect;
import fyi.sorenneedscoffee.eyecandy.http.Endpoint;
import fyi.sorenneedscoffee.eyecandy.http.requests.TimeShiftModel;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;
import fyi.sorenneedscoffee.eyecandy.util.Point;

import java.util.UUID;

public class TimeShiftEndpoint extends Endpoint {

    public TimeShiftEndpoint() {
        super("POST");
    }

    @Override
    protected void process(HttpExchange httpExchange, String body) throws Exception {
        if (isInvalid(body)) {
            respond(httpExchange, 400);
            return;
        }

        UUID id = UUID.fromString(queryToMap(httpExchange.getRequestURI().getQuery()).get("id"));
        TimeShiftModel request = EyeCandy.gson.fromJson(body, TimeShiftModel.class);
        Point point = EyeCandy.pointUtil.getPoint(request.pointId);

        EffectGroup group = new EffectGroup(id);
        group.add(new TimeShiftEffect(point, request.amount, request.period));

        EffectManager.startEffect(group);

        respond(httpExchange, 200);
    }

    @Override
    protected boolean checkPath(String path) {
        return path.contains("start");
    }
}
