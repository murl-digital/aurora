package digital.murl.aurora.effects;

import digital.murl.aurora.Plugin;

import java.util.Map;

@SuppressWarnings("unused")
public class Effects {
    public static String executeEffectAction(String agentName, String actionName, Map<String, Object> params) {
        if (!Plugin.plugin.isEnabled()) return null;

        return EffectManager.executeEffectAction(agentName, actionName, params);
    }

    public static String executeEffectAction(String id, String agentName, String actionName, Map<String, Object> params) {
        if (!Plugin.plugin.isEnabled()) return null;

        return EffectManager.executeEffectAction(id, agentName, actionName, params);
    }

    public static String createEffect(String agentName, Map<String, Object> params) {
        if (!Plugin.plugin.isEnabled()) return null;

        return EffectManager.createEffect(agentName, params);
    }

    public static String createEffect(String id, String agentName, Map<String, Object> params) {
        if (!Plugin.plugin.isEnabled()) return null;

        return EffectManager.createEffect(id, agentName, params);
    }
}
