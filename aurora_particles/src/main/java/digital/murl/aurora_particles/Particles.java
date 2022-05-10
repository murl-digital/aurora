package digital.murl.aurora_particles;

import com.google.gson.internal.LinkedTreeMap;
import digital.murl.aurora.Aurora;
import digital.murl.aurora.Plugin;
import digital.murl.aurora.Result;
import digital.murl.aurora.effects.*;
import digital.murl.aurora.effects.Effect;
import digital.murl.aurora.points.Points;
import digital.murl.aurora.regions.Regions;
import digital.murl.aurora.regions.distributors.Distributor;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Particles implements Effect {
    Location[] locations = new Location[0];
    Location[] pointLocations = new Location[0];
    Location[] regionLocations = new Location[0];

    Distributor distributor = null;

    Particle particle = Particle.CLOUD;
    Object particleOptions = null;

    BukkitTask task;
    Runnable runnable = () -> {
        distribute();

        for (Location l : locations)
            l.getWorld().spawnParticle(particle, l, 1, 0,0, 0, 0, particleOptions);

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
        if (params.containsKey("points"))
            pointLocations = Points.getPointLocation(params.get("points"));

        if (params.containsKey("region")) {
            List<Object> dParams = (List<Object>)params.get("region");
            distributor = Regions.getDistributor((String)dParams.get(1),
                    Regions.getRegion((String)dParams.get(0)),
                    (LinkedTreeMap<String, Object>)dParams.get(2));
        }

        if (params.containsKey("particle")) {
            Map<String, Object> settings = (LinkedTreeMap<String, Object>)params.get("particle");
            particle = Particle.valueOf((String)settings.get("type"));
            particleOptions = null;
            switch (particle) {
                case REDSTONE:
                    String colortext = (String)settings.get("color");
                    if (colortext.startsWith("#")) colortext = colortext.substring(1);
                    Color color = Color.fromRGB(
                            Integer.parseInt(colortext.substring(0,2),16),
                            Integer.parseInt(colortext.substring(2,4),16),
                            Integer.parseInt(colortext.substring(4,6),16));
                    particleOptions = new Particle.DustOptions(color, ((Double)settings.get("size")).floatValue());
                    break;
                case DUST_COLOR_TRANSITION:
                    String from = (String)settings.get("from");
                    if (from.startsWith("#")) from = from.substring(1);
                    Color fromColor = Color.fromRGB(
                            Integer.parseInt(from.substring(0,2),16),
                            Integer.parseInt(from.substring(2,4),16),
                            Integer.parseInt(from.substring(4,6),16));

                    String to = (String)settings.get("to");
                    if (to.startsWith("#")) to = to.substring(1);
                    Color toColor = Color.fromRGB(
                            Integer.parseInt(to.substring(0,2),16),
                            Integer.parseInt(to.substring(2,4),16),
                            Integer.parseInt(to.substring(4,6),16));

                    particleOptions = new Particle.DustTransition(fromColor, toColor, ((Double)settings.get("size")).floatValue());
                    break;
                case ITEM_CRACK:
                    Material material = Material.valueOf((String)settings.get("material"));
                    if (material.isItem()) particleOptions = new ItemStack(material);
                    break;
                case BLOCK_CRACK:
                case BLOCK_DUST:
                case FALLING_DUST:
                case BLOCK_MARKER:
                    Material material1 = Material.valueOf((String)settings.get("material"));
                    particleOptions = material1.createBlockData();
                    break;
            }


        }

        distribute();

        if (params.containsKey("active"))
            if ((boolean)params.get("active") == true) return on();
            else return off();

        return new EffectAction.ActionResult(Result.Outcome.SUCCESS, "", EffectAction.ActiveState.DEFAULT);
    }

    public EffectAction.ActionResult distribute() {
        if (distributor != null)
            regionLocations = distributor.distribute();

        locations = new Location[pointLocations.length + regionLocations.length];
        for (int i = 0; i < pointLocations.length; i++)
            locations[i] = pointLocations[i];
        for (int i = pointLocations.length; i < pointLocations.length + regionLocations.length; i++)
            locations[i] = regionLocations[i-pointLocations.length];

        return new EffectAction.ActionResult(Result.Outcome.SUCCESS, "", EffectAction.ActiveState.DEFAULT);
    }

    public EffectAction.ActionResult trigger() {
        runnable.run();
        return new EffectAction.ActionResult(Result.Outcome.SUCCESS, "", EffectAction.ActiveState.DEFAULT);
    }

    public EffectAction.ActionResult on() {
        if (task == null)
            task = Bukkit.getScheduler().runTaskTimer(Plugin.plugin, runnable, 0, 4);
        return new EffectAction.ActionResult(Result.Outcome.SUCCESS, "", EffectAction.ActiveState.ACTIVE);
    }

    public EffectAction.ActionResult off() {
        if (task != null)
            task.cancel();
        task = null;
        return new EffectAction.ActionResult(Result.Outcome.SUCCESS, "", EffectAction.ActiveState.INACTIVE);
    }

    public static void registerEffect() {
        HashMap<String, EffectAction> actions = new HashMap<>();
        HashMap<String, String> schemas = new HashMap<>();

        EffectAction<Particles, Map<String, Object>> set = (a, p) -> a.set(p);
        actions.put("Set", set);

        EffectAction<Particles, Map<String, Object>> trigger = (a, p) -> a.trigger();
        actions.put("Trigger", trigger);

        EffectAction<Particles, Map<String, Object>> distribute = (a, p) -> a.distribute();
        actions.put("Distribute", distribute);

        EffectAction<Particles, Map<String, Object>> on = (a, p) -> a.on();
        actions.put("On", on);

        EffectAction<Particles, Map<String, Object>> off = (a, p) -> a.off();
        actions.put("Off", off);

        try {
            Aurora.registerEffect("Particles", Particles.class, actions, CacheBehavior.NORMAL, schemas);;
        } catch (Exception e) {

        }
    }
}
