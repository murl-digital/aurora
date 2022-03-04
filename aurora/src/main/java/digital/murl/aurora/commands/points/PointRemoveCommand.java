package digital.murl.aurora.commands.points;

import digital.murl.aurora.points.Points;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.PlayerCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

public class PointRemoveCommand extends PlayerCommand {

    public PointRemoveCommand(PointCommand root) {
        super(root, "remove");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull Player sender, @NotNull String[] params) {
        Points.remove(params);
        Points.save();

        return CommandResult.SUCCESS;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull Player sender, @NotNull String[] params) {
        if (params.length > 0)
            return TabResult.of("", Points.getGroups().keySet());

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
