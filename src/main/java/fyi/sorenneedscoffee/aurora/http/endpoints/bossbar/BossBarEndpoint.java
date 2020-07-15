package fyi.sorenneedscoffee.aurora.http.endpoints.bossbar;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.bossbar.BossBarEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.bossbar.BossBarModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.boss.BarColor;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/effects/{id}/bar")
public class BossBarEndpoint extends Endpoint {

    @Path("/start")
    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id") UUID id, BossBarModel model) {
        if (EffectManager.exists(id))
            return BAD_REQUEST;

        try {
            Point point = Aurora.pointUtil.getPoint(0);
            if (point == null)
                return POINT_DOESNT_EXIST;

            BarColor color;
            try {
                color = BarColor.valueOf(model.color);
            } catch (IllegalArgumentException e) {
                return UNPROCESSABLE_ENTITY;
            }

            EffectGroup group = new EffectGroup(id);
            group.add(new BossBarEffect(id, color, model.title));

            EffectManager.startEffect(group);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR;
        }
    }
}
