package fyi.sorenneedscoffee.eyecandy.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.implementations.DragonEffect;
import fyi.sorenneedscoffee.eyecandy.http.requests.DragonRequest;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;
import fyi.sorenneedscoffee.eyecandy.util.Point;
import fyi.sorenneedscoffee.eyecandy.util.PointUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class DragonHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            DragonRequest request = new Gson().fromJson(new InputStreamReader(httpExchange.getRequestBody(), "UTF-8"), DragonRequest.class);
            Point point = EyeCandy.pointUtil.getPoint(request.pointId);
            DragonEffect effect = new DragonEffect();
            effect.isStatic = request.isStatic;

            EffectManager.doAction(request.action, effect, point);
            httpExchange.sendResponseHeaders(200, 0);
        } catch (UnsupportedEncodingException e) {
            httpExchange.sendResponseHeaders(500, 0);
        }
    }
}
