package digital.murl.aurora.regions;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface RegionParameterCompleter {
    String[] parameterCompleter(Player sender, String[] params);
}
