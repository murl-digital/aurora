package digital.murl.aurora.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import digital.murl.aurora.effects.EffectRegistrar;
import digital.murl.aurora.effects.Effects;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.CombinedCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

import java.util.HashMap;
import java.util.Map;

public class EffectCommand extends CombinedCommand {
    public EffectCommand() {
        super("effects");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull CommandSender sender, @NotNull String[] params) {
        if (params.length < 3) return CommandResult.WRONG_SYNTAX;

        Map<String, Object> effectParams = new HashMap<>();

        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(params[3], JsonObject.class);
            effectParams = gson.fromJson(json, HashMap.class);
        } catch (Exception e) {
            for (int i = 3; i < params.length; i++) {
                String[] tag = params[i].split("=");
                if (tag.length < 2) continue;
                effectParams.put(tag[0],tag[1]);
            }
        }

        if (Effects.executeEffectAction(params[0], params[1], params[2], effectParams) == null)
            return CommandResult.FAILURE;

        return CommandResult.SUCCESS;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull CommandSender sender, @NotNull String[] params) {
        if (params.length != 3) return TabResult.EMPTY_RESULT;
        try {
            return TabResult.of("", EffectRegistrar.getEffectActions(params[1]).keySet());
        } catch (Exception e) {
            return TabResult.EMPTY_RESULT;
        }
    }

    @NotNull
    @Override
    public PermissionWrapper getPermission() {
        return PermissionWrapper.of("test");
    }

    @NotNull
    @Override
    public String getSyntax() {
        return "test syntax";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "test description";
    }
}
