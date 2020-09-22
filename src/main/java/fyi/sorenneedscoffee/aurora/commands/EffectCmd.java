package fyi.sorenneedscoffee.aurora.commands;

import fyi.sorenneedscoffee.aurora.util.EffectManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EffectCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (args[0]) {
            case "stopall":
                if (sender.hasPermission("aurora.admin")) {
                    EffectManager.stopAll(false);
                    sender.sendMessage("All current effects have been stopped.");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "nope");
                return false;
        }
        return false;
    }
}
