package fyi.sorenneedscoffee.aurora.http.endpoints;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.time.TimeShiftEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.TimeShiftModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.UUID;

@Path("effects/time/{id}")
public class TimeShiftEndpoint extends Endpoint {

    @Path("/start")
    @POST
    @Consumes("application/json")
    public Response startEffect(@PathParam("id") UUID id, InputStream stream) {
        if (EffectManager.exists(id))
            return Response.status(400).build();

        try {
            byte[] target = new byte[stream.available()];
            stream.read(target);
            String in = new String(target);

            if (isInvalid(in, TimeShiftModel[].class)) {
                return Response.status(400).build();
            }

            TimeShiftModel[] request = Aurora.gson.fromJson(in, TimeShiftModel[].class);
            Point point = Aurora.pointUtil.getPoint(0);
            if (point == null)
                return Response.status(400).build();

            EffectGroup group = new EffectGroup(id);
            for (TimeShiftModel model : request) {
                group.add(new TimeShiftEffect(point, model.amount));
            }

            EffectManager.startEffect(group);
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }
}
