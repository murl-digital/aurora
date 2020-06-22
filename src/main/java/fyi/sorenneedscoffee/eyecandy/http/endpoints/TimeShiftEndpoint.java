package fyi.sorenneedscoffee.eyecandy.http.endpoints;

import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.EffectGroup;
import fyi.sorenneedscoffee.eyecandy.effects.time.TimeShiftEffect;
import fyi.sorenneedscoffee.eyecandy.http.Endpoint;
import fyi.sorenneedscoffee.eyecandy.http.requests.TimeShiftModel;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;
import fyi.sorenneedscoffee.eyecandy.util.Point;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.UUID;

@Path("effects/{id}/start/time")
public class TimeShiftEndpoint extends Endpoint {

    @POST
    @Consumes("application/json")
    public Response startEffect(@PathParam("id") UUID id, InputStream stream) {
        try {
            byte[] target = new byte[stream.available()];
            stream.read(target);
            String in = new String(target);

            if (isInvalid(in)) {
                return Response.status(400).build();
            }

            TimeShiftModel[] request = EyeCandy.gson.fromJson(in, TimeShiftModel[].class);
            Point point = EyeCandy.pointUtil.getPoint(0);

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
