package fyi.sorenneedscoffee.eyecandy.http.endpoints;

import com.sun.net.httpserver.HttpExchange;
import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.Effect;
import fyi.sorenneedscoffee.eyecandy.effects.EffectGroup;
import fyi.sorenneedscoffee.eyecandy.effects.potion.GlobalPotionEffect;
import fyi.sorenneedscoffee.eyecandy.http.Endpoint;
import fyi.sorenneedscoffee.eyecandy.http.requests.GlobalPotionModel;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;
import fyi.sorenneedscoffee.eyecandy.util.Point;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class GlobalPotionEndpoint extends Endpoint {

    public GlobalPotionEndpoint() {
        super("POST");
    }

    @Override
    protected void process(HttpExchange httpExchange, String body) throws Exception {
        if (isInvalid(body)) {
            respond(httpExchange, 400);
            return;
        }

        UUID id = UUID.fromString(queryToMap(httpExchange.getRequestURI().getQuery()).get("id"));
        GlobalPotionModel model = EyeCandy.gson.fromJson(body, GlobalPotionModel.class);
        Point point = EyeCandy.pointUtil.getPoint(0);

        EffectGroup group = new EffectGroup(id);
        PotionEffectType type = PotionEffectType.getByName(model.potionType);
        if(type == null) {
            respond(httpExchange, 422);
            return;
        }
        group.add(new GlobalPotionEffect(point, type, model.amplifier));

        EffectManager.startEffect(group);

        respond(httpExchange, 200);
    }

    @Override
    protected boolean checkPath(String path) {
        return path.contains("start");
    }
}
