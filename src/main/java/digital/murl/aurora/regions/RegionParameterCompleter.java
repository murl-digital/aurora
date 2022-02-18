package digital.murl.aurora.regions;

import org.bukkit.entity.Player;
import xyz.tozymc.spigot.api.command.result.TabResult;

@FunctionalInterface
public interface RegionParameterCompleter {
    TabResult parameterCompleter(Player sender, String[] params);
}
