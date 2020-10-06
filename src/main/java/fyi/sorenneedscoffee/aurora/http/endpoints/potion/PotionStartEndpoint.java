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
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.potion.PotionEffectType;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

@StaticEffect("potion")
public class PotionStartEndpoint extends Endpoint {

    public PotionStartEndpoint() {
        this.path = Pattern.compile("/effects/potion/.+/start");
    }

    private static Response constructGroup(GlobalPotionModel[] models, Point point, EffectGroup group) throws Throwable {
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

    private Response start(UUID id, GlobalPotionModel[] models) {
        try {
            if (EffectManager.exists(id))
                return BAD_REQUEST;

            Point point = Aurora.pointUtil.getPoint(0);
            if (point == null)
                return POINT_DOESNT_EXIST;

            EffectGroup group = new EffectGroup(id);
            return constructGroup(models, point, group);
        } catch (Throwable e) {
            if (e.getMessage() != null) Aurora.logger.warning(e.getMessage());
            Aurora.logger.warning(ExceptionUtils.getStackTrace(e));
            String message = e.getMessage() != null ? e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e) : ExceptionUtils.getStackTrace(e);
            return SERVER_ERROR.clone().entity(message).build();
        }
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
        } catch (Throwable e) {
            if (e.getMessage() != null) Aurora.logger.warning(e.getMessage());
            Aurora.logger.warning(ExceptionUtils.getStackTrace(e));
            String message = e.getMessage() != null ? e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e) : ExceptionUtils.getStackTrace(e);
            return SERVER_ERROR.clone().entity(message).build();
        }
    }
}
