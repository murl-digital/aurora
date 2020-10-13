package fyi.sorenneedscoffee.aurora.http.endpoints.potion;

import fyi.sorenneedscoffee.aurora.effects.potion.PotionEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class PotionStopEndpoint extends Endpoint {

    public PotionStopEndpoint() {
        this.path = Pattern.compile("/effects/potion/stop");
    }

    @Override
    public Response handle(String[] tokens, InputStreamReader bodyStream) {
        EffectManager.stopType(PotionEffect.class);
        return OK;
    }
}
