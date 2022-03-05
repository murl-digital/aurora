package digital.murl.aurora.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import digital.murl.aurora.Plugin;
import digital.murl.aurora.agents.AgentManager;
import digital.murl.aurora.effects.EffectManager;
import digital.murl.aurora.effects.EffectRegistrar;
import digital.murl.aurora.effects.Effects;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.CombinedCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class EffectCommand extends CombinedCommand {
    public EffectCommand() {
        super("effects");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull CommandSender sender, @NotNull String[] params) {
        if (params.length < 2) return CommandResult.WRONG_SYNTAX;

        Map<String, Object> effectParams = new HashMap<>();

        if (params.length == 2) {
            Effects.createEffect(params[0], params[1], new HashMap<>());
            return CommandResult.SUCCESS;
        }

        String rawJson = String.join(" ", Arrays.copyOfRange(params, 3, params.length));
        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(rawJson, JsonObject.class);
            effectParams = gson.fromJson(json, HashMap.class);
        } catch (Exception e) {
            Plugin.logger.log(Level.WARNING, "Could not deserialize agent json payload: ", e);
            Plugin.logger.log(Level.WARNING, "JSON: " + rawJson);
        }

        if (Effects.executeEffectAction(params[0], params[1], params[2], effectParams) == null)
            return CommandResult.FAILURE;

        return CommandResult.SUCCESS;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull CommandSender sender, @NotNull String[] params) {
        if (params.length == 1)
            return TabResult.of("", EffectManager.getAllEffectInstances());

        if (params.length == 2)
            return TabResult.of("", EffectRegistrar.getAllEffectNames());

        if (params.length == 3)
            return TabResult.of("", EffectRegistrar.getEffectActions(params[1]).keySet());

        return TabResult.EMPTY_RESULT;
    }

    @NotNull
    @Override
    public PermissionWrapper getPermission() {
        return PermissionWrapper.of("test");
    }

    @NotNull
    @Override
    public String getSyntax() {
        return "/effects effect_name effect_type effect_action [payload]";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Execute effect actions or create effect instance.";
    }
}
