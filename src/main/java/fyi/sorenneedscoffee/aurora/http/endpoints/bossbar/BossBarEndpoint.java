package fyi.sorenneedscoffee.aurora.http.endpoints.bossbar;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.bossbar.BossBarEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.bossbar.BossBarModel;
import fyi.sorenneedscoffee.aurora.util.BarManager;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.boss.BarColor;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("bar/")
public class BossBarEndpoint extends Endpoint {

    @Path("/set")
    @Operation(
            summary = "why do i hear boss music?",
            description = "Displays a boss bar on players' screens that can contain certain information. This is seperate from all other effects and will not be affected by the stop endpoint.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The effect group started successfully"),
                    @ApiResponse(responseCode = "400", description = "There is either a problem with the request body or an effect with the given UUID is already active."),
                    @ApiResponse(responseCode = "501", description = "Point 0 must be present for this effect to work."),
                    @ApiResponse(responseCode = "422", description = "The given BarColor is not valid.")
            }
    )
    @POST
    @Consumes("application/json")
    public static Response start(
            @RequestBody(description = "BossBar model", required = true)
                    BossBarModel[] models) {
        try {
            Point point = Aurora.pointUtil.getPoint(0);
            if (point == null)
                return POINT_DOESNT_EXIST;

            EffectGroup group = new EffectGroup(UUID.randomUUID());

            for (BossBarModel model : models) {
                BarColor color;
                try {
                    color = BarColor.valueOf(model.color);
                } catch (IllegalArgumentException e) {
                    return UNPROCESSABLE_ENTITY;
                }

                group.add(new BossBarEffect(UUID.randomUUID(), color, model.title));
            }

            if(BarManager.isActive()) {
                BarManager.clearBars();
            }
            BarManager.showBar(group);

            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    @Path("/clear")
    @Operation(
            summary = "oh wait the music's gone now",
            description =  "If there are boss bars currently being displayed, they will be cleared. If not, nothing will happen.",
            responses = @ApiResponse(responseCode = "200", description = "Any bars that were being displayed are cleared.")
    )
    @POST
    public static Response clear() {
        try {
            if(!BarManager.isActive())
                return OK;

            BarManager.clearBars();
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }
}
