package fyi.sorenneedscoffee.eyecandy.commands;

import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.util.Point;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PointCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if(args[0] == null) {
                sender.sendMessage("Args cannot be blank");
            }
            if(!args[0].equals("add")) {
                sender.sendMessage("Invalid argument. Usage: /point add");
                return false;
            } else {
                return AddPoint.execute(sender, command, label, new String[]{});
            }
        }
        sender.sendMessage("You can only use this command as a player");
        return false;
    }

    private static class AddPoint {
        protected static boolean execute(CommandSender sender, Command command, String label, String[] args) {
            EyeCandy.dataManager.addPointToFile(new Point(0, ((Player) sender).getLocation()));
            return true;
        }
    }
}
