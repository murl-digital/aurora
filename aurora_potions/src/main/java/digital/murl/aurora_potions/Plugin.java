package digital.murl.aurora_potions;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Plugin extends JavaPlugin {
    public static Plugin plugin;
    public static Logger logger;

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();

        Potion.registerEffect();
    }

    @Override
    public void onDisable() {

    }
}
