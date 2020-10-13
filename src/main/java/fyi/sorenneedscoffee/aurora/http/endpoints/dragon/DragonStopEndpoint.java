package fyi.sorenneedscoffee.aurora.http.endpoints.dragon;

import fyi.sorenneedscoffee.aurora.effects.dragon.DragonEffect;
import fyi.sorenneedscoffee.aurora.effects.laser.LaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class DragonStopEndpoint extends Endpoint {

    public DragonStopEndpoint() {
        this.path = Pattern.compile("/effects/dragon/stop");
    }

    @Override
    public Response handle(String[] tokens, InputStreamReader bodyStream) {
        EffectManager.stopType(DragonEffect.class);
        return OK;
    }
}
