package fyi.sorenneedscoffee.aurora.commands;

import fyi.sorenneedscoffee.aurora.util.EffectManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EffectCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (args[0]) {
            case "stopall":
                EffectManager.stopAll(false);
                sender.sendMessage("All current effects have been stopped.");
                return true;
        }
        return false;
    }
}
