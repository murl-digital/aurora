package fyi.sorenneedscoffee.eyecandy.http.endpoints;

import com.sun.net.httpserver.HttpExchange;
import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.EffectGroup;
import fyi.sorenneedscoffee.eyecandy.effects.particle.ParticleEffect;
import fyi.sorenneedscoffee.eyecandy.effects.particle.Region;
import fyi.sorenneedscoffee.eyecandy.http.Endpoint;
import fyi.sorenneedscoffee.eyecandy.http.requests.ParticleModel;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;
import fyi.sorenneedscoffee.eyecandy.util.Point;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParticleEndpoint extends Endpoint {

    public ParticleEndpoint() {
        super("POST");
    }

    @Override
    protected void process(HttpExchange httpExchange, String body) throws Exception {
        if (isInvalid(body)) {
            respond(httpExchange, 400);
            return;
        }

        String sId = queryToMap(httpExchange.getRequestURI().getQuery()).get("id");
        UUID id;
        if (sId == null)
            id = null;
        else
            id = UUID.fromString(sId);

        ParticleModel[] request = EyeCandy.gson.fromJson(body, ParticleModel[].class);
        EffectGroup group = new EffectGroup(id);
        for(ParticleModel model : request) {
            List<Point> points = new ArrayList<>();
            for(int i : model.region.pointIds) {
                points.add(EyeCandy.pointUtil.getPoint(i));
            }
            Region region = new Region(points.toArray(new Point[0]), model.region.type, model.region.density, model.region.randomized, model.region.equation);
            Particle particle;
            try {
                particle = Particle.valueOf(model.name);
            } catch (IllegalArgumentException e) {
                respond(httpExchange, 422);
                return;
            }
            ParticleEffect effect = new ParticleEffect(region, particle);
            group.add(effect);
        }

        if(httpExchange.getRequestURI().getPath().contains("start"))
            EffectManager.startEffect(group);
        else if(httpExchange.getRequestURI().getPath().contains("trigger"))
            EffectManager.triggerEffect(group);

        respond(httpExchange, 200);
    }

    @Override
    protected boolean checkPath(String path) {
        return path.contains("start") || path.contains("trigger");
    }
}
