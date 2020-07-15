package fyi.sorenneedscoffee.aurora.http.endpoints.particle;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.particle.ParticleEffect;
import fyi.sorenneedscoffee.aurora.effects.particle.Region;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.particle.ParticleModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Particle;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("effects/particle/{id}")
public class ParticleEndpoint extends Endpoint {

    @Path("/start")
    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id") UUID id, ParticleModel[] request) {
        if (EffectManager.exists(id))
            return Response.status(400).build();
        try {
            EffectGroup group;
            try {
                group = constructGroup(id, request, false);
            } catch (IllegalArgumentException e) {
                return UNPROCESSABLE_ENTITY;
            }

            EffectManager.startEffect(group);
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR;
        }

        return OK;
    }

    @Path("/trigger")
    @POST
    @Consumes("application/json")
    public Response trigger(@PathParam("id") UUID id, ParticleModel[] request) {
        try {
            EffectGroup group;
            try {
                group = constructGroup(id, request, true);
            } catch (IllegalArgumentException e) {
                return UNPROCESSABLE_ENTITY;
            } catch (NullPointerException e) {
                return BAD_REQUEST;
            }

            EffectManager.triggerEffect(group);
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR;
        }

        return OK;
    }

    private EffectGroup constructGroup(UUID id, ParticleModel[] request, boolean ignoreRandomized) throws IllegalArgumentException, NullPointerException {
        EffectGroup group = new EffectGroup(id);
        for (ParticleModel model : request) {
            List<Point> points = new ArrayList<>();
            for (int i : model.region.pointIds) {
                Point point = Aurora.pointUtil.getPoint(i);
                if (point == null)
                    throw new NullPointerException();
                points.add(Aurora.pointUtil.getPoint(i));
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
