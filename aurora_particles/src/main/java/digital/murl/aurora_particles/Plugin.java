package digital.murl.aurora_particles;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Plugin extends JavaPlugin {
    public static Plugin plugin;
    public static Logger logger;

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();

        Particles.registerEffect();
    }

    @Override
    public void onDisable() {

    }
}
