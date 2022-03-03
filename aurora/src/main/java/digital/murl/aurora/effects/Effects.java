package digital.murl.aurora.effects;

import digital.murl.aurora.Plugin;
import digital.murl.aurora.Result;

import java.util.Map;

@SuppressWarnings("unused")
public class Effects {
    /**
     * Executes an action on an existing effect that was created through {@code createEffect}
     *
     * @param id         The effect id
     * @param effectName The name of the effect
     * @param actionName The action name
     * @param params     The parameters that will be passed to the action
     * @return A {@code Result} that indicates the result of running the action.
     */
    public static Result executeEffectAction(String id, String effectName, String actionName, Map<String, Object> params) {
        if (!Plugin.plugin.isEnabled()) return pluginNotLoaded();

        return EffectManager.executeEffectAction(id, effectName, actionName, params);
    }

    /**
     * Creates and inits a new effect with a randomly generated id
     *
     * @param effectName The effect type
     * @param params     The params to pass to the effect init method
     * @return A result with the outcome of creating the effect. If the outcome is {@code SUCCESS}, the {@code message} is the new effect's id.
     */
    public static Result createEffect(String effectName, Map<String, Object> params) {
        if (!Plugin.plugin.isEnabled()) return pluginNotLoaded();

        return EffectManager.createEffect(effectName, params);
    }

    /**
     * Creates and inits a new effect with the given id
     *
     * @param id         The id to use when creating the effect
     * @param effectName The effect type
     * @param params     The params to pass to the effect init method
     * @return A result with the outcome of creating the effect. If the outcome is {@code SUCCESS}, the {@code message} is the new effect's id.
     */
    public static Result createEffect(String id, String effectName, Map<String, Object> params) {
        if (!Plugin.plugin.isEnabled()) return pluginNotLoaded();

        return EffectManager.createEffect(id, effectName, params);
    }

    private static Result pluginNotLoaded() {
        return new Result(Result.Outcome.ERROR, "The plugin isn't loaded right now! Patience, grasshopper");
    }
}
