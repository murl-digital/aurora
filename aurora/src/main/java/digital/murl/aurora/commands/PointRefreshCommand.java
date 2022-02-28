package digital.murl.aurora.commands;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.points.Points;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.PlayerCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

import java.io.IOException;

public class PointRefreshCommand extends PlayerCommand {

    public PointRefreshCommand(PointCommand root) {
        super(root, "refresh");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull Player sender, @NotNull String[] params) {
        if (params.length > 0) {
            return CommandResult.WRONG_SYNTAX;
        }

        try {
            Points.refresh();
        } catch (IOException e) {
            Aurora.logger.warning("Refresh failed: " + e.getMessage());
        }

        return CommandResult.SUCCESS;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull Player sender, @NotNull String[] params) {
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
