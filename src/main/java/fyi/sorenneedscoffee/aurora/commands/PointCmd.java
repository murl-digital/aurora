package fyi.sorenneedscoffee.aurora.commands;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PointCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args[0].equals("reload") || args[0].equals("refresh")) {
            Aurora.pointUtil.refresh();
            return true;
        }

        if (sender instanceof Player) {
            if (args[0].equals("add")) {
                return AddPoint.execute(sender, args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : null);
            } else if (args[0].equals("remove")) {
                return RemovePoint.execute(sender, args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : null);
            } else {
                return false;
            }
        }
        sender.sendMessage("You can only use this command as a player");
        return false;
    }

    private static class AddPoint {
        protected static boolean execute(CommandSender sender, String[] args) {
            int id = Aurora.pointUtil.getAvailableId();
            if (args == null)
                Aurora.pointUtil.savePoint(new Point(id, ((Player) sender).getLocation()));
            else {
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Invalid arguments. Expected: /point add <x> <y> <z> (XYZ should be absolute)");
                    return false;
                }
                Aurora.dataManager.addPointToFile(new Point(id, new Location(
                        ((Player) sender).getWorld(),
                        Double.parseDouble(args[0]),
                        Double.parseDouble(args[1]),
                        Double.parseDouble(args[2])
                )));
            }
            sender.sendMessage("Created a new point with id " + id);
            return true;
        }
    }

    private static class RemovePoint {
        protected static boolean execute(CommandSender sender, String[] args) {
            if (args == null || args[0] == null) {
                sender.sendMessage(ChatColor.RED + "You must provide a valid ID");
                return false;
            }

            try {
                int id = Integer.parseInt(args[0]);
                Aurora.dataManager.removePointFromFile(id);
                sender.sendMessage("Point " + id + " removed.");
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "You must provide a valid ID");
                return false;
            }
        }
    }
}
