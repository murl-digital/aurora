package digital.murl.aurora.commands;

import digital.murl.aurora.points.Points;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.PlayerCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

public class PointAddCommand extends PlayerCommand {

    public PointAddCommand(PointCommand root) {
        super(root, "add");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull Player sender, @NotNull String[] params) {
        if (params.length == 0) {
            Points.addPoint(sender.getLocation());
            return CommandResult.SUCCESS;
        }

        double x = Double.parseDouble(params[0]);
        double y = Double.parseDouble(params[1]);
        double z = Double.parseDouble(params[2]);

        Location location = new Location(sender.getWorld(), x, y, z);
        Points.addPoint(location);

        return CommandResult.SUCCESS;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull Player sender, @NotNull String[] params) {
        if (params.length == 1) {
            Location location = sender.getLocation();
            return TabResult.of("", String.format("%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
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
