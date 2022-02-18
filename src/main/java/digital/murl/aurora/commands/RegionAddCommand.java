package digital.murl.aurora.commands;

import digital.murl.aurora.regions.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.PlayerCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

import java.util.HashMap;
import java.util.Map;

public class RegionAddCommand extends PlayerCommand {

    private static Map<String, RegionParameterConstructor> constructors = new HashMap<>();
    private static Map<String, RegionParameterCompleter> completers = new HashMap<>();

    public static void addRegionConstructor(String name, RegionParameterConstructor constructor) {
        constructors.put(name.toLowerCase(), constructor);
    }

    public static void addRegionCompleter(String name, RegionParameterCompleter completer) {
        completers.put(name.toLowerCase(), completer);
    }

    public RegionAddCommand(RegionCommand root) {
        super(root, "add");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull Player sender, @NotNull String[] params) {
        if (params.length < 2)
            return CommandResult.WRONG_SYNTAX;

        if (constructors.containsKey(params[1])) {
            RegionParameterConstructor constructor = constructors.get(params[1]);
            if (constructor != null)
                return constructor.regionConstructor(sender, params);
            CommandResult.from(CommandResult.Type.FAILURE, "This region type doesn't have a registered constructor.");
        }

        return CommandResult.WRONG_SYNTAX;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull Player sender, @NotNull String[] params) {
        if (params.length < 2) return TabResult.EMPTY_RESULT;

        if (params.length == 2) return sender instanceof Player
            ? TabResult.of("", completers.keySet())
            : TabResult.EMPTY_RESULT;

        if (completers.containsKey(params[1])) {
            RegionParameterCompleter completer = completers.get(params[1]);
            if (completer != null)
                return completer.parameterCompleter(sender, params);
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
