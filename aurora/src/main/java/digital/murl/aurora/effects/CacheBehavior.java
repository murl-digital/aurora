package digital.murl.aurora.effects;

public enum CacheBehavior {
    /**
     * With standard cache behavior, whenever an agent is created it'll be cached for a period of time which is configured
     * by the user. The cache is automatically emptied when the plugin unloads.
     */
    NORMAL,
    /**
     * Persistent agents will remain until the plugin unloads. They will not be cleared before then.
     */
    PERSISTENT
}
