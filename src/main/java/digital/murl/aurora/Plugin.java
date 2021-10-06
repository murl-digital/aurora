package digital.murl.aurora;

import com.alibaba.fastjson.JSON;
import digital.murl.aurora.commands.TestCommand;
import digital.murl.aurora.effects.Effect;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.tozymc.spigot.api.command.CommandController;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Aurora.logger = getLogger();
        Aurora.logger.info("it works!");
        Aurora.commandController = new CommandController(this);
        Aurora.commandController.addCommand(new TestCommand());

        JSON.parseObject()
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
