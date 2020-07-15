package fyi.sorenneedscoffee.aurora.http.endpoints.time;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.time.TimeShiftEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.time.TimeShiftModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

@Path("effects/time/{id}")
public class TimeShiftEndpoint extends Endpoint {

    @Path("/start")
    @POST
    @Consumes("application/json")
    public Response startEffect(@PathParam("id") UUID id, TimeShiftModel[] request) {
        if (EffectManager.exists(id))
            return BAD_REQUEST;

        try {
            Point point = Aurora.pointUtil.getPoint(0);
            if (point == null)
                return POINT_DOESNT_EXIST;

            EffectGroup group = new EffectGroup(id);
            for (TimeShiftModel model : request) {
                group.add(new TimeShiftEffect(point, model.amount));
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
