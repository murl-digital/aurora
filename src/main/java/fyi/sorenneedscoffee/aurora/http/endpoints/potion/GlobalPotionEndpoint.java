package fyi.sorenneedscoffee.aurora.http.endpoints.potion;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.potion.GlobalPotionEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.potion.GlobalPotionModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import fyi.sorenneedscoffee.aurora.util.annotation.StaticAction;
import fyi.sorenneedscoffee.aurora.util.annotation.StaticEffect;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.potion.PotionEffectType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("effects/potion/{id}")
@StaticEffect("potion")
public class GlobalPotionEndpoint extends Endpoint {

    @Path("/start")
    @Operation(
            summary = "Global potion effect",
            description = "Applies given potion effect(s) to all players on the server",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The effect group started successfully"),
                    @ApiResponse(responseCode = "400", description = "An effect group with the given uuid exists and is already active"),
                    @ApiResponse(responseCode = "501", description = "This effect requires that point 0 is already defined, the endpoint will respond with 501 if a point 0 is not found."),
                    @ApiResponse(responseCode = "422", description = "The provided potion effect type is not recognized by the server.")
            },
            externalDocs = @ExternalDocumentation(
                    description = "List of available potion effect types",
                    url = "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html"
            )
    )
    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id")
                          @Parameter(description = "UUID that will be assigned to the effect group", required = true)
                                  UUID id,
                          @RequestBody(description = "Array of GlobalPotion models", required = true)
                                  GlobalPotionModel[] models) {
        try {
            if (EffectManager.exists(id))
                return BAD_REQUEST;

            Point point = Aurora.pointUtil.getPoint(0);
            if (point == null)
                return POINT_DOESNT_EXIST;

            EffectGroup group = new EffectGroup(id);
            return constructGroup(models, point, group);
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    @StaticAction
    public Response startStatic(GlobalPotionModel[] models) {
        try {
            Point point = Aurora.pointUtil.getPoint(0);
            if (point == null)
                return POINT_DOESNT_EXIST;

            EffectGroup group = new EffectGroup(UUID.randomUUID(), true);
            return constructGroup(models, point, group);
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    private Response constructGroup(GlobalPotionModel[] models, Point point, EffectGroup group) throws Exception {
        for (GlobalPotionModel model : models) {
            PotionEffectType type = PotionEffectType.getByName(model.potionType);
            if (type == null) {
                return UNPROCESSABLE_ENTITY;
            }
            group.add(new GlobalPotionEffect(point, type, model.amplifier));
        }

        EffectManager.startEffect(group);
        return OK;
    }
}
