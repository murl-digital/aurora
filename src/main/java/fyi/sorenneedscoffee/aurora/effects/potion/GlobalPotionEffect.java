package fyi.sorenneedscoffee.aurora.effects.potion;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.Effect;
import fyi.sorenneedscoffee.aurora.effects.EffectAction;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import static org.bukkit.Bukkit.getScheduler;

public class GlobalPotionEffect extends Effect {
    private final World world;
    private final PotionEffect potionEffect;
    private BukkitTask task;

    public GlobalPotionEffect(Point point, PotionEffectType type, int amplifier) {
        world = point.getLocation().getWorld();
        //potion amplifiers are 0 based apparently, this is to accommodate that.
        this.potionEffect = new PotionEffect(type, 225, amplifier - 1, false, false, false);
    }

    @Override
    public void init() {
    }

    @Override
    public void execute(EffectAction action) {
        if (action == EffectAction.START) {
            task = getScheduler().runTaskTimer(Aurora.plugin, () -> {
                for (Player player : world.getPlayers()) {
                    player.removePotionEffect(potionEffect.getType());
                    player.addPotionEffect(potionEffect);
                }
            }, 0, 20);
        } else if (action == EffectAction.STOP) {
            task.cancel();
        }
    }

    @Override
    public void cleanup() {
        runTask(() -> {
            for (Player player : world.getPlayers()) {
                player.removePotionEffect(potionEffect.getType());
            }
        });
    }
}
