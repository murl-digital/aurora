package fyi.sorenneedscoffee.aurora.http.endpoints.particle;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.particle.ParticleEffect;
import fyi.sorenneedscoffee.aurora.effects.particle.Region;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.particle.ParticleModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class ParticleEndpoint extends Endpoint {

    public ParticleEndpoint() {
        this.path = Pattern.compile("/effects/particle/.+/(start|trigger)");
    }

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
    public static Response start(
            @Parameter(description = "UUID that will be assigned to the effect group", required = true)
                    UUID id,
            @RequestBody(description = "Array of Particle models", required = true)
                    ParticleModel[] models) {
        try {
            if (EffectManager.exists(id))
                return BAD_REQUEST;

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
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

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
    public static Response trigger(
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
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    private static EffectGroup constructGroup(UUID id, ParticleModel[] models, boolean ignoreRandomized) throws IllegalArgumentException, NullPointerException {
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
            Object options = null;
            switch (particle) {
                case REDSTONE:
                    options = new Particle.DustOptions(model.options.dustColor, model.options.dustSize);
                    break;
                case ITEM_CRACK:
                    Material material = Material.valueOf(model.options.materialName);
                    if (!material.isItem())
                        throw new IllegalArgumentException();
                    options = new ItemStack(material);
                    break;
                case BLOCK_CRACK:
                case BLOCK_DUST:
                case FALLING_DUST:
                    Material material1 = Material.valueOf(model.options.materialName);
                    options = material1.createBlockData();
                    break;
            }
            ParticleEffect effect = new ParticleEffect(region, particle, options);
            group.add(effect);
        }

        return group;
    }

    @Override
    public Response handle(String[] tokens, InputStreamReader bodyStream) {
        try {
            UUID id = UUID.fromString(tokens[2]);
            ParticleModel[] models = Aurora.gson.fromJson(bodyStream, ParticleModel[].class);
            switch (tokens[3]) {
                case "start":
                    return start(id, models);
                case "trigger":
                    return trigger(id, models);
                default:
                    return NOT_FOUND;
            }
        } catch (IllegalArgumentException e) {
            return BAD_REQUEST;
        }
    }
}
