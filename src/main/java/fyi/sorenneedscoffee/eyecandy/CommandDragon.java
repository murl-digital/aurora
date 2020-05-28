package fyi.sorenneedscoffee.eyecandy;

import fyi.sorenneedscoffee.eyecandy.effects.implementations.DragonEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandDragon implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        DragonEffect.execute(new Location(
                        Bukkit.getWorld(args[0]),
                        Double.parseDouble(args[1]),
                        Double.parseDouble(args[2]),
                        Double.parseDouble(args[3])
                )
        );
        return true;
    }
}
