package fyi.sorenneedscoffee.aurora.http.endpoints;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.potion.GlobalPotionEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.GlobalPotionModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.bukkit.potion.PotionEffectType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.UUID;

@Path("/effects/potion/{id}")
public class GlobalPotionEndpoint extends Endpoint {

    @Path("/start")
    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id") UUID id, InputStream stream) {
        if (EffectManager.exists(id))
            return Response.status(400).build();
        try {
            byte[] target = new byte[stream.available()];
            stream.read(target);
            String in = new String(target);

            if (isInvalid(in, GlobalPotionModel[].class)) {
                return Response.status(400).build();
            }

            GlobalPotionModel[] request = Aurora.gson.fromJson(in, GlobalPotionModel[].class);
            Point point = Aurora.pointUtil.getPoint(0);
            if (point == null)
                return Response.status(400).build();

            EffectGroup group = new EffectGroup(id);
            for (GlobalPotionModel model : request) {
                PotionEffectType type = PotionEffectType.getByName(model.potionType);
                if (type == null) {
                    return Response.status(422).build();
                }
                group.add(new GlobalPotionEffect(point, type, model.amplifier));
            }

            EffectManager.startEffect(group);
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }
}
