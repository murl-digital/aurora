package fyi.sorenneedscoffee.aurora.http.endpoints.potion;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.potion.GlobalPotionEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
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

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

@StaticEffect("potion")
public class GlobalPotionEndpoint extends Endpoint {

    public GlobalPotionEndpoint() {
        this.path = Pattern.compile("/effects/potion/.+/start");
    }

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
    public static Response start(
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

    private static Response constructGroup(GlobalPotionModel[] models, Point point, EffectGroup group) throws Exception {
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

    @Override
    public Response handle(String[] tokens, InputStreamReader bodyStream) {
        try {
            UUID id = UUID.fromString(tokens[2]);
            GlobalPotionModel[] models = Aurora.gson.fromJson(bodyStream, GlobalPotionModel[].class);
            return start(id, models);
        } catch (IllegalArgumentException e) {
            return BAD_REQUEST;
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
}
