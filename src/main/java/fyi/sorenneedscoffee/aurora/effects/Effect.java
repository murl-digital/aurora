package fyi.sorenneedscoffee.aurora.effects;

import fyi.sorenneedscoffee.aurora.Aurora;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;

public abstract class Effect {

  public abstract void init() throws Exception;

  public abstract void execute(EffectAction action);

  public abstract void cleanup();

  protected void runTask(Runnable runnable) {
    try {
      Bukkit.getScheduler().runTask(Aurora.plugin, runnable);
    } catch (IllegalPluginAccessException e) {
      runnable.run();
    }
  }
}
