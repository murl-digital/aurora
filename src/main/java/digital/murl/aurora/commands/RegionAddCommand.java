package digital.murl.aurora.commands;

import digital.murl.aurora.regions.*;
import org.bukkit.Location;
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

        if (params[1].equals("world")) {
            if (params.length != 2)
                return CommandResult.WRONG_SYNTAX;

            Regions.addRegion(new RegionWorld(params[0], sender.getLocation().getWorld().getName()));
            Regions.save();

            return CommandResult.SUCCESS;
        }

        if (params[1].equals("sphere")) {
            if (params.length != 6)
                return CommandResult.WRONG_SYNTAX;

            double[] p = new double[4];
            try {
                Location l = sender.getLocation();
                double[] sp = new double[] {l.getX(),l.getY(),l.getZ()};
                for (int i = 0; i < 3; i++) {
                    if (params[i+2].equals("~"))
                        p[i] = sp[i];
                    else if (params[i+2].charAt(0) == '~')
                        p[i] = sp[i] + Double.parseDouble(params[i+2].substring(1));
                    else
                        p[i] = Double.parseDouble(params[i+2]);
                }
            } catch (Exception e) {
                return CommandResult.from(CommandResult.Type.FAILURE, "Couldn't parse coordinates.");
            }
            double r;
            try {
                r = Double.parseDouble(params[5]);
            } catch (Exception e) {
                return CommandResult.from(CommandResult.Type.FAILURE, "Couldn't parse radius.");
            }
            Regions.addRegion(new RegionSphere(params[0], sender.getLocation().getWorld().getName(), p[0], p[1], p[2], r));
            Regions.save();

            return CommandResult.SUCCESS;
        }

        if (params[1].equals("cuboid")) {
            if (params.length != 8)
                return CommandResult.WRONG_SYNTAX;

            double[] p = new double[6];
            try {
                Location l = sender.getLocation();
                double[] sp = new double[] {l.getX(),l.getY(),l.getZ(),l.getX(),l.getY(),l.getZ()};
                for (int i = 0; i < 6; i++) {
                    if (params[i+2].equals("~"))
                        p[i] = sp[i];
                    else if (params[i+2].charAt(0) == '~')
                        p[i] = sp[i] + Double.parseDouble(params[i+2].substring(1));
                    else
                        p[i] = Double.parseDouble(params[i+2]);
                }
            } catch (Exception e) {
                return CommandResult.from(CommandResult.Type.FAILURE, "Couldn't parse coordinates.");
            }
            Regions.addRegion(new RegionCuboid(params[0], sender.getLocation().getWorld().getName(), p[0], p[1], p[2], p[3], p[4], p[5]));
            Regions.save();

            return CommandResult.SUCCESS;
        }

        return CommandResult.WRONG_SYNTAX;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull Player sender, @NotNull String[] params) {
        if (params.length < 2) return TabResult.EMPTY_RESULT;

        if (params.length == 2) return sender instanceof Player
            ? TabResult.of("", "world", "sphere", "cuboid")
            : TabResult.EMPTY_RESULT;

        Location location = sender.getLocation();

        if (params[1].equals("sphere") || params[1].equals("cuboid")) {
            if (params.length == 3)
                return TabResult.of("",
                    String.format("%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ()), "~ ~ ~");
            if (params.length == 4)
                return TabResult.of("",
                    String.format("%d %d", location.getBlockY(), location.getBlockZ()), "~ ~");
            if (params.length == 5)
                return TabResult.of("",
                    String.format("%d", location.getBlockZ()), "~");
        }

        if (params[1].equals("cuboid")) {
            if (params.length == 6)
                return TabResult.of("",
                    String.format("%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ()), "~ ~ ~");
            if (params.length == 7)
                return TabResult.of("",
                    String.format("%d %d", location.getBlockY(), location.getBlockZ()+1), "~ ~");
            if (params.length == 8)
                return TabResult.of("",
                    String.format("%d", location.getBlockZ()), "~");
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
