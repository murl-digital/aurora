package fyi.sorenneedscoffee.aurora.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EffectCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /*EffectAction action = EffectAction.valueOf(args[0]);
        if(args[1].equals("dragon")) {
            DragonEffect effect = new DragonEffect();
            Point point = Aurora.pointUtil.getPoint(Integer.parseInt(args[2]));
            effect.isStatic = Boolean.parseBoolean(args[3]);
            EffectManager.doAction(action, effect, point);
            return true;
        }*/
        return false;
    }
}