package fyi.sorenneedscoffee.aurora.http.endpoints.potion;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.potion.GlobalPotionEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.potion.GlobalPotionModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.potion.PotionEffectType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/effects/potion/{id}")
public class GlobalPotionEndpoint extends Endpoint {

    @Path("/start")
    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id") UUID id, GlobalPotionModel[] request) {
        if (EffectManager.exists(id))
            return BAD_REQUEST;

        try {
            Point point = Aurora.pointUtil.getPoint(0);
            if (point == null)
                return POINT_DOESNT_EXIST;

            EffectGroup group = new EffectGroup(id);
            for (GlobalPotionModel model : request) {
                PotionEffectType type = PotionEffectType.getByName(model.potionType);
                if (type == null) {
                    return UNPROCESSABLE_ENTITY;
                }
                group.add(new GlobalPotionEffect(point, type, model.amplifier));
            }

            EffectManager.startEffect(group);
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR;
        }

        return OK;
    }
}
