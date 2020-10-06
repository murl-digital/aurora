package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.laser.LaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.laser.LaserModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class LaserStartEndpoint extends Endpoint {

    public LaserStartEndpoint() {
        this.path = Pattern.compile("/effects/laser/.+/start");
    }

    public static Response start(UUID id, LaserModel[] request) {
        try {
            if (EffectManager.exists(id))
                return BAD_REQUEST;

            EffectGroup group = new EffectGroup(id);
            for (LaserModel model : request) {
                Point start = Aurora.pointUtil.getPoint(model.startId);
                Point end = Aurora.pointUtil.getPoint(model.endId);
                if (start == null || end == null)
                    return POINT_DOESNT_EXIST;
                group.add(new LaserEffect(start, end));
            }

            EffectManager.startEffect(group);
            return OK;
        } catch (Throwable e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    @Override
    public Response handle(String[] tokens, InputStreamReader bodyStream) {
        try {
            UUID id = UUID.fromString(tokens[2]);
            LaserModel[] models = Aurora.gson.fromJson(bodyStream, LaserModel[].class);
            return start(id, models);
        } catch (IllegalArgumentException e) {
            return BAD_REQUEST;
        }
    }
}