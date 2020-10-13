package fyi.sorenneedscoffee.aurora.http.endpoints.potion;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.potion.PotionEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.potion.PotionModel;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;
import fyi.sorenneedscoffee.aurora.points.Point;
import fyi.sorenneedscoffee.aurora.annotations.StaticAction;
import fyi.sorenneedscoffee.aurora.annotations.StaticEffect;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.potion.PotionEffectType;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

@StaticEffect("potion")
public class PotionStartEndpoint extends Endpoint {

    public PotionStartEndpoint() {
        this.path = Pattern.compile("/effects/potion/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/start");
    }

    private static Response constructGroup(PotionModel[] models, Point point, EffectGroup group) throws Throwable {
        for (PotionModel model : models) {
            PotionEffectType type = PotionEffectType.getByName(model.potionType);
            if (type == null) {
                return UNPROCESSABLE_ENTITY;
            }
            group.add(new PotionEffect(point, type, model.amplifier));
        }

        EffectManager.startEffect(group);
        return OK;
    }

    private Response start(UUID id, PotionModel[] models) {
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
            PotionModel[] models = Aurora.gson.fromJson(bodyStream, PotionModel[].class);
            return start(id, models);
        } catch (IllegalArgumentException e) {
            return BAD_REQUEST;
        }
    }

    @StaticAction
    public Response startStatic(PotionModel[] models) {
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
