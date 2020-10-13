package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.effects.laser.LaserEffect;
import fyi.sorenneedscoffee.aurora.effects.laser.TargetedLaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class TargetedLaserStopEndpoint extends Endpoint {

    public TargetedLaserStopEndpoint() {
        this.path = Pattern.compile("/effects/targetedlaser/stop");
    }

    @Override
    public Response handle(String[] tokens, InputStreamReader bodyStream) {
        EffectManager.stopType(TargetedLaserEffect.class);
        return OK;
    }
}
