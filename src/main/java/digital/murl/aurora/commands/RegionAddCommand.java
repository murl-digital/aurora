package digital.murl.aurora.commands;

import digital.murl.aurora.regions.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.PlayerCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

public class RegionAddCommand extends PlayerCommand {

    public RegionAddCommand(RegionCommand root) {
        super(root, "add");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull Player sender, @NotNull String[] params) {
        if (params.length < 2)
            return CommandResult.WRONG_SYNTAX;

        RegionParameterConstructor constructor = Regions.getParameterConstructor(params[1]);
        if (constructor != null) {
            RegionParameterConstructor.Result result = constructor.regionConstructor(sender, params);
            if (result.output.isEmpty())
                return result.success ? CommandResult.SUCCESS : CommandResult.FAILURE;
            return CommandResult.from(result.success ? CommandResult.Type.SUCCESS : CommandResult.Type.FAILURE, result.output);
        }
        return CommandResult.from(CommandResult.Type.FAILURE, "This region type doesn't have a registered constructor.");
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull Player sender, @NotNull String[] params) {
        if (params.length < 2) return TabResult.EMPTY_RESULT;

        if (params.length == 2) return sender instanceof Player
            ? TabResult.of("", Regions.getRegionTypes())
            : TabResult.EMPTY_RESULT;

        RegionParameterCompleter completer = Regions.getParameterCompleter(params[1]);
        if (completer != null) {
            String[] results = completer.parameterCompleter(sender, params);
            if (results == null || results.length == 0) return TabResult.EMPTY_RESULT;
            return TabResult.of("", results);
        }

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
        return "";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "";
    }
}
