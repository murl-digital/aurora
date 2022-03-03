package digital.murl.aurora;

import com.dslplatform.json.DslJson;
import digital.murl.aurora.effects.Action;
import digital.murl.aurora.effects.Effect;
import digital.murl.aurora.effects.EffectRegistrar;
import digital.murl.aurora.effects.CacheBehavior;
import digital.murl.aurora.regions.*;
import digital.murl.aurora.regions.distributors.RegionDistributor;

import java.util.HashMap;

@SuppressWarnings("unused")
public class Aurora {
    public static <T extends Effect> void registerEffect(String name, Class<T> effect, HashMap<String, Action> actions, CacheBehavior cacheBehavior) throws Exception {
        EffectRegistrar.addEffect(name, effect, actions, cacheBehavior);
    }

    public static void registerRegionType(String type,
                                          RegionMapConstructor mapConstructor,
                                          RegionParameterConstructor parameterConstructor,
                                          RegionParameterCompleter parameterCompleter) {
        Regions.addMapConstructor(type, mapConstructor);
        Regions.addParameterConstructor(type, parameterConstructor);
        Regions.addParameterCompleter(type, parameterCompleter);
    }

    public static void registerRegionDistributor(String type, RegionDistributor distributor) {
        Regions.addRegionDistributor(type, distributor);
    }
}
