package digital.murl.aurora;

import digital.murl.aurora.commands.PointAddCommand;
import digital.murl.aurora.commands.PointCommand;
import digital.murl.aurora.points.Points;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.tozymc.spigot.api.command.CommandController;

import java.io.IOException;

public final class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Aurora.plugin = this;
        Aurora.logger = getLogger();
        Aurora.logger.info("it works!");

        PointCommand pointCommand = new PointCommand();
        PointAddCommand pointAddCommand = new PointAddCommand(pointCommand);

        Aurora.commandController = new CommandController(this);
        Aurora.commandController.addCommand(pointCommand);
        Aurora.commandController.addCommand(pointAddCommand);

        if (getDataFolder().mkdirs()) {

        }

        try {
            Points.load();
        } catch (IOException e) {
            Aurora.logger.severe("Failed to load points: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
