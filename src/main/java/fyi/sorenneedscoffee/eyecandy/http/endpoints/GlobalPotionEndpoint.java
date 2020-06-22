package fyi.sorenneedscoffee.eyecandy.http.endpoints;

import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.EffectGroup;
import fyi.sorenneedscoffee.eyecandy.effects.potion.GlobalPotionEffect;
import fyi.sorenneedscoffee.eyecandy.http.Endpoint;
import fyi.sorenneedscoffee.eyecandy.http.requests.GlobalPotionModel;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;
import fyi.sorenneedscoffee.eyecandy.util.Point;
import org.bukkit.potion.PotionEffectType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.UUID;

@Path("/effects/{id}/start/potion")
public class GlobalPotionEndpoint extends Endpoint {

    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id") UUID id, InputStream stream) {
        try {
            byte[] target = new byte[stream.available()];
            stream.read(target);
            String in = new String(target);

            if (isInvalid(in)) {
                return Response.status(400).build();
            }

            GlobalPotionModel[] request = EyeCandy.gson.fromJson(in, GlobalPotionModel[].class);
            Point point = EyeCandy.pointUtil.getPoint(0);

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
