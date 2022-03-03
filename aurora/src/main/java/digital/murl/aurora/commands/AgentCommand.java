package digital.murl.aurora.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import digital.murl.aurora.agents.AgentManager;
import digital.murl.aurora.agents.Agents;
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

public class AgentCommand extends CombinedCommand {
    public AgentCommand() {
        super("agents");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull CommandSender sender, @NotNull String[] params) {
        if (params.length < 2) return CommandResult.WRONG_SYNTAX;

        Map<String, Object> agentParams = new HashMap<>();

        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(params[2], JsonObject.class);
            agentParams = gson.fromJson(json, HashMap.class);
        } catch (Exception e) {
            for (int i = 2; i < params.length; i++) {
                String[] tag = params[i].split("=");
                if (tag.length < 2) continue;
                agentParams.put(tag[0],tag[1]);
            }
        }

        if (Agents.executeAgentAction(params[0], params[1], agentParams) == null)
            return CommandResult.FAILURE;

        return CommandResult.SUCCESS;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull CommandSender sender, @NotNull String[] params) {
        if (params.length == 1)
            return TabResult.of("", AgentManager.getAllAgentNames());

        if (params.length == 2)
            return TabResult.of("", AgentManager.getAgentActions(params[1]).keySet());

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
        return "test syntax";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "test description";
    }
}
