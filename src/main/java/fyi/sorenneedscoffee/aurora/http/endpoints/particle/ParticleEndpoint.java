package fyi.sorenneedscoffee.aurora.http.endpoints.particle;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.particle.ParticleEffect;
import fyi.sorenneedscoffee.aurora.effects.particle.Region;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.particle.ParticleModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            summary = "Allows for the spawning of particles in interesting ways",
            description = "The premise for how particle spawning works is the endpoint takes 2 points and creates a region with them. There are 3 different region types; POINTS, CUBOID, and EQUATION. " +
                    "POINTS just takes all the points you feed the endpoint and spawns particle there, CUBOID takes the first 2 points you feed the endpoint and creates a cuboid with the points as corners, and EQUATION is the same as cuboid but you can provide a 3d graphing equation. " +
                    "The start endpoint spawns the particles once every 5 game ticks.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The effect group started successfully"),
                    @ApiResponse(responseCode = "400", description = "There's a problem with the request body, or an effect group with the given uuid exists and is already active"),
                    @ApiResponse(responseCode = "501", description = "One of the point ids in the request does not have a point ingame."),
                    @ApiResponse(responseCode = "422", description = "The endpoint did not recognize the given particle type")
            }
    )
    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id")
                          @Parameter(description = "UUID that will be assigned to the effect group", required = true)
                                  UUID id,
                          @RequestBody(description = "Array of Particle models", required = true)
                                  ParticleModel[] models) {
        if (EffectManager.exists(id))
            return BAD_REQUEST;

        try {
            EffectGroup group;

            try {
                group = constructGroup(id, models, false);
            } catch (IllegalArgumentException e) {
                return UNPROCESSABLE_ENTITY;
            } catch (NullPointerException e) {
                return POINT_DOESNT_EXIST;
            }

            EffectManager.startEffect(group);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR;
        }
    }

    @Path("/trigger")
    @Operation(
            summary = "Allows for the spawning of particles in interesting ways",
            description = "The premise for how particle spawning works is the endpoint takes 2 points and creates a region with them. There are 3 different region types; POINTS, CUBOID, and EQUATION. " +
                    "POINTS just takes all the points you feed the endpoint and spawns particle there, CUBOID takes the first 2 points you feed the endpoint and creates a cuboid with the points as corners, and EQUATION is the same as cuboid but you can provide a 3d graphing equation. " +
                    "The trigger endpoint spawns the particles only once.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The effect group started successfully"),
                    @ApiResponse(responseCode = "400", description = "There's a problem with the request body, or an effect group with the given uuid exists and is already active"),
                    @ApiResponse(responseCode = "501", description = "One of the point ids in the request does not have a point ingame."),
                    @ApiResponse(responseCode = "422", description = "The endpoint did not recognize the given particle type")
            }
    )
    @POST
    @Consumes("application/json")
    public Response trigger(@PathParam("id")
                            @Parameter(description = "UUID that will be assigned to the effect group", required = true)
                                    UUID id,
                            ParticleModel[] models) {
        try {
            EffectGroup group;
            try {
                group = constructGroup(id, models, true);
            } catch (IllegalArgumentException e) {
                return UNPROCESSABLE_ENTITY;
            } catch (NullPointerException e) {
                return POINT_DOESNT_EXIST;
            }

            EffectManager.triggerEffect(group);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR;
        }
    }

    private EffectGroup constructGroup(UUID id, ParticleModel[] models, boolean ignoreRandomized) throws IllegalArgumentException, NullPointerException {
        EffectGroup group = new EffectGroup(id);
        for (ParticleModel model : models) {
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