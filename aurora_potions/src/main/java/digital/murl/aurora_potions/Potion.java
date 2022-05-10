package digital.murl.aurora_potions;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.Result;
import digital.murl.aurora.effects.CacheBehavior;
import digital.murl.aurora.effects.Effect;
import digital.murl.aurora.effects.EffectAction;
import digital.murl.aurora.regions.Region;
import digital.murl.aurora.regions.Regions;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class Potion implements Effect {
    PotionEffect potion = null;
    PotionEffectType type = null;
    int amplifier = 0;
    int duration = 10;
    Region region = null;

    BukkitTask task;
    Runnable runnable = () -> {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (region != null && region.collisionCheck(p.getLocation()))
                p.addPotionEffect(potion);
            else p.removePotionEffect(type);
        }
    };

    @Override
    public Result init(Map<String, Object> map) {
        return new Result(Result.Outcome.SUCCESS, "");
    }

    @Override
    public void cleanup() {
        if (task != null)
            task.cancel();
        task = null;
    }

    public EffectAction.ActionResult set(Map<String, Object> params) {
        if (params.containsKey("type")) {
            type = PotionEffectType.getByName((String)params.get("type"));
        }

        if (params.containsKey("amplifier")) {
            amplifier = ((Double)params.get("amplifier")).intValue() - 1;
        }

        if (params.containsKey("duration")) {
            amplifier = ((Double)params.get("duration")).intValue();
        }

        potion = new PotionEffect(type, duration, amplifier);

        if (params.containsKey("region")) {
            region = Regions.getRegion((String)params.get("region"));
        }

        if (params.containsKey("active"))
            if ((boolean)params.get("active")) {
                if (task == null)
                    task = Bukkit.getScheduler().runTaskTimer(Plugin.plugin, runnable, 0, 4);
                return new EffectAction.ActionResult(Result.Outcome.SUCCESS, "", EffectAction.ActiveState.ACTIVE);
            } else {
                if (task != null) {
                    task.cancel();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.removePotionEffect(type);
                    }
                }
                task = null;
                return new EffectAction.ActionResult(Result.Outcome.SUCCESS, "", EffectAction.ActiveState.INACTIVE);
            }

        return new EffectAction.ActionResult(Result.Outcome.SUCCESS, "", EffectAction.ActiveState.DEFAULT);
    }

    public static void registerEffect() {
        HashMap<String, EffectAction> actions = new HashMap<>();
        HashMap<String, String> schemas = new HashMap<>();

        EffectAction<Potion, Map<String, Object>> set = (a, p) -> a.set(p);
        actions.put("Set", set);

        try {
            Aurora.registerEffect("Potion", Potion.class, actions, CacheBehavior.NORMAL, schemas);;
        } catch (Exception e) {

        }
    }
}
