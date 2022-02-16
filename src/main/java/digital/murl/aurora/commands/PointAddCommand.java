package digital.murl.aurora.commands;

import digital.murl.aurora.points.Points;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
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

        if (params.length < 3) {
            sender.spigot().sendMessage(notEnoughCoords(params.length));
            return CommandResult.WRONG_SYNTAX;
        }

        double[] p = new double[3];

        try {
            Location l = sender.getLocation();
            double[] sp = new double[] {l.getX(),l.getY(),l.getZ()};
            for (int i = 0; i < 3; i++) {
                if (params[i].equals("~"))
                    p[i] = sp[i];
                else if (params[i].charAt(0) == '~')
                    p[i] = sp[i] + Double.parseDouble(params[i].substring(1));
                else
                    p[i] = Double.parseDouble(params[i]);
            }
        } catch (Exception e) {
            return CommandResult.from(CommandResult.Type.FAILURE, "Couldn't parse coordinates.");
        }

        Location location = new Location(sender.getWorld(), p[0], p[1], p[2]);

        if (params.length > 3)
            Points.addPoint(location, params[3]);
        else Points.addPoint(location);

        return CommandResult.SUCCESS;
    }

    @NotNull
    private BaseComponent[] notEnoughCoords(int count) {
        return new ComponentBuilder().append(String.format("Expected 3 coordinates. Got %d", count)).color(ChatColor.RED).create();
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull Player sender, @NotNull String[] params) {
        if (params.length == 1) {
            Location location = sender.getLocation();
            return TabResult.of("",
                String.format("%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                "~ ~ ~");
        }
        if (params.length == 4)
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
