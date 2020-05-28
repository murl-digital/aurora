package fyi.sorenneedscoffee.eyecandy;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fyi.sorenneedscoffee.eyecandy.effects.implementations.DragonEffect;
import fyi.sorenneedscoffee.eyecandy.util.EntityHider;
import org.bukkit.plugin.java.JavaPlugin;

public final class EyeCandy extends JavaPlugin {
    public static EntityHider hider;
    public static EyeCandy plugin;
    public static ProtocolManager manager;

    @Override
    public void onEnable() {
        //hider = new EntityHider(this, EntityHider.Policy.BLACKLIST);
        manager = ProtocolLibrary.getProtocolManager();
        plugin = this;

        manager.addPacketListener(new DragonEffect.DragonPacketListener(this));

        getServer().getPluginManager().registerEvents(new DragonEffect.DragonListener(), this);

        this.getCommand("dragon").setExecutor(new CommandDragon());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
