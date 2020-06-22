package fyi.sorenneedscoffee.eyecandy.http.endpoints;

import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.EffectGroup;
import fyi.sorenneedscoffee.eyecandy.effects.particle.ParticleEffect;
import fyi.sorenneedscoffee.eyecandy.effects.particle.Region;
import fyi.sorenneedscoffee.eyecandy.http.Endpoint;
import fyi.sorenneedscoffee.eyecandy.http.requests.ParticleModel;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;
import fyi.sorenneedscoffee.eyecandy.util.Point;
import org.bukkit.Particle;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParticleEndpoint extends Endpoint {

    @Path("/effects/{id}/start/particle")
    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id") UUID id, InputStream stream) {
        try {
            byte[] target = new byte[stream.available()];
            stream.read(target);
            String in = new String(target);

            if (isInvalid(in)) {
                return Response.status(400).build();
            }

            EffectGroup group;
            try {
                group = constructGroup(id, in, false);
            } catch (IllegalArgumentException e) {
                return Response.status(244).build();
            }

            EffectManager.startEffect(group);
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @Path("/effects/{id}/trigger/particle")
    @POST
    @Consumes("application/json")
    public Response trigger(@PathParam("id") UUID id, InputStream stream) {
        try {
            byte[] target = new byte[stream.available()];
            stream.read(target);
            String in = new String(target);

            if (isInvalid(in)) {
                return Response.status(400).build();
            }

            EffectGroup group;
            try {
                group = constructGroup(id, in, true);
            } catch (IllegalArgumentException e) {
                return Response.status(244).build();
            }

            EffectManager.triggerEffect(group);
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    private EffectGroup constructGroup(UUID id, String jsonString, boolean ignoreRandomized) throws IllegalArgumentException {
        ParticleModel[] request = EyeCandy.gson.fromJson(jsonString, ParticleModel[].class);
        EffectGroup group = new EffectGroup(id);
        for (ParticleModel model : request) {
            List<Point> points = new ArrayList<>();
            for (int i : model.region.pointIds) {
                points.add(EyeCandy.pointUtil.getPoint(i));
            }
            Region region = new Region(
                    points.toArray(new Point[0]),
                    model.region.type,
                    model.region.density,
                    !ignoreRandomized && model.region.randomized,
                    model.region.equation
            );
            Particle particle;
            particle = Particle.valueOf(model.name);
            ParticleEffect effect = new ParticleEffect(region, particle);
            group.add(effect);
        }

        return group;
    }
}
