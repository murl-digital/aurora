package fyi.sorenneedscoffee.aurora.http.endpoints.time;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.time.TimeShiftEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.time.TimeShiftModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class TimeShiftStartEndpoint extends Endpoint {

    public TimeShiftStartEndpoint() {
        this.path = Pattern.compile("/effects/time/.+/start");
    }

    private Response start(UUID id, TimeShiftModel[] models) {
        try {
            if (EffectManager.exists(id))
                return BAD_REQUEST;

            Point point = Aurora.pointUtil.getPoint(0);
            if (point == null)
                return POINT_DOESNT_EXIST;

            EffectGroup group = new EffectGroup(id);
            for (TimeShiftModel model : models) {
                group.add(new TimeShiftEffect(point, model.amount));
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
            TimeShiftModel[] models = Aurora.gson.fromJson(bodyStream, TimeShiftModel[].class);
            return start(id, models);
        } catch (IllegalArgumentException e) {
            return BAD_REQUEST;
        }
    }
}
