package fyi.sorenneedscoffee.aurora.http.endpoints;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.bossbar.BossBarEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.BossBarModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.bukkit.boss.BarColor;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

@Path("/effects/{id}/bar")
public class BossBarEndpoint extends Endpoint {

    @Path("/start")
    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id") UUID id, InputStream stream) {
        if (EffectManager.exists(id))
            return Response.status(400).build();

        try {
            Reader reader = new InputStreamReader(stream);

            if(isInvalid(reader, BossBarModel.class))
                return Response.status(400).build();

            BossBarModel model = Aurora.gson.fromJson(reader, BossBarModel.class);
            Point point = Aurora.pointUtil.getPoint(0);
            if (point == null)
                return Response.status(400).build();

            BarColor color;
            try {
                color = BarColor.valueOf(model.color);
            } catch (IllegalArgumentException e) {
                return Response.status(422).build();
            }

            EffectGroup group = new EffectGroup(id);
            group.add(new BossBarEffect(id, color, model.title));

            EffectManager.startEffect(group);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }
}
