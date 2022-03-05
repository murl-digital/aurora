package digital.murl.aurora.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import digital.murl.aurora.Plugin;
import digital.murl.aurora.Result;
import digital.murl.aurora.agents.AgentManager;
import digital.murl.aurora.agents.Agents;
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

public class AgentCommand extends CombinedCommand {
    public AgentCommand() {
        super("agents");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull CommandSender sender, @NotNull String[] params) {
        if (params.length < 2) return CommandResult.WRONG_SYNTAX;

        Map<String, Object> agentParams = new HashMap<>();

        String rawJson = String.join(" ", Arrays.copyOfRange(params, 2, params.length));
        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(rawJson, JsonObject.class);
            agentParams = gson.fromJson(json, HashMap.class);
        } catch (Exception e) {
            Plugin.logger.log(Level.WARNING, "Could not deserialize agent json payload: ", e);
            Plugin.logger.log(Level.WARNING, "JSON: " + rawJson);
        }

        Result result = Agents.executeAgentAction(params[0], params[1], agentParams);
        sender.sendMessage(result.outcome.toString() + ": " + result.message);

        return result.outcome == Result.Outcome.SUCCESS ? CommandResult.SUCCESS : CommandResult.FAILURE;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull CommandSender sender, @NotNull String[] params) {
        if (params.length == 1)
            return TabResult.of("", AgentManager.getAllAgentNames());

        if (params.length == 2)
            return TabResult.of("", AgentManager.getAgentActions(params[0]).keySet());

        return TabResult.EMPTY_RESULT;
    }

    @NotNull
    @Override
    public PermissionWrapper getPermission() {
        return PermissionWrapper.of("test"); // fixme
    }

    @NotNull
    @Override
    public String getSyntax() {
        return "/agents agent_name agent_action [payload]";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Execute agent actions";
    }
}
