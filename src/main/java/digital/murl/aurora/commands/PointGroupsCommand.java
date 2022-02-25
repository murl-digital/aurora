package digital.murl.aurora.commands;

import digital.murl.aurora.points.Points;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.PlayerCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

import java.util.Arrays;

public class PointGroupsCommand extends PlayerCommand {

    public PointGroupsCommand(PointCommand root) {
        super(root, "groups");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull Player sender, @NotNull String[] params) {
        if (params.length < 2)
            return CommandResult.WRONG_SYNTAX;

        if (params[0].equals("add")) {
            if (params.length < 3)
                return CommandResult.WRONG_SYNTAX;
            for (String param : Arrays.copyOfRange(params, 2, params.length)) {
                try {
                    int id = Integer.parseInt(param);
                    Points.addPointToGroup(id,params[1]);
                } catch (Exception e) {
                    try {
                        for (int id : Points.getId(param))
                            Points.addPointToGroup(id,params[1]);
                    } catch (Exception e2) {
                        sender.spigot().sendMessage(new ComponentBuilder().append(String.format("Couldn't parse %s", param)).color(ChatColor.RED).create());
                    }
                }
            }

            Points.save();
            return CommandResult.SUCCESS;
        }

        if (params[0].equals("remove")) {
            if (params.length == 2)
                Points.removeGroup(params[1]);

            else for (String param : Arrays.copyOfRange(params, 2, params.length)) {
                try {
                    int id = Integer.parseInt(param);
                    Points.removePointFromGroup(id,params[1]);
                } catch (Exception e) {
                    try {
                        for (int id : Points.getId(param))
                            Points.removePointFromGroup(id,params[1]);
                    } catch (Exception e2) {
                        sender.spigot().sendMessage(new ComponentBuilder().append(String.format("Couldn't parse %s", param)).color(ChatColor.RED).create());
                    }
                }
            }

            Points.save();
            return CommandResult.SUCCESS;
        }

        return CommandResult.WRONG_SYNTAX;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull Player sender, @NotNull String[] params) {
        if (params.length == 1)
            return TabResult.of("", "add", "remove");

        if (params.length > 1)
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
