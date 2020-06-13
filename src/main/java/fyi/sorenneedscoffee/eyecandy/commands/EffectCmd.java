package fyi.sorenneedscoffee.eyecandy.commands;

import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.EffectAction;
import fyi.sorenneedscoffee.eyecandy.effects.implementations.DragonEffect;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;
import fyi.sorenneedscoffee.eyecandy.util.Point;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EffectCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        EffectAction action = EffectAction.valueOf(args[0]);
        if(args[1].equals("dragon")) {
            DragonEffect effect = new DragonEffect();
            Point point = EyeCandy.pointUtil.getPoint(Integer.parseInt(args[2]));
            effect.isStatic = Boolean.parseBoolean(args[3]);
            EffectManager.doAction(action, effect, point);
            return true;
        }
        return false;
    }
}
