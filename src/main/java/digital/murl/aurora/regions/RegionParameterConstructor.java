package digital.murl.aurora.regions;

import org.bukkit.entity.Player;
import xyz.tozymc.spigot.api.command.result.CommandResult;

@FunctionalInterface
public interface RegionParameterConstructor {
    CommandResult regionConstructor(Player sender, String[] params);
}
