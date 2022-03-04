package digital.murl.aurora.commands.points;

import digital.murl.aurora.points.Point;
import digital.murl.aurora.points.Points;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.CombinedCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

import java.util.List;
import java.util.Map;


public class PointCommand extends CombinedCommand {
    public PointCommand() {
        super("points");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull CommandSender sender, @NotNull String[] params) {
        sender.spigot().sendMessage(new ComponentBuilder().append("All points and groups:").color(ChatColor.AQUA).create());
        for (Point point : Points.getPoints()) {
            if (point == null) continue;
            BaseComponent[] components = new ComponentBuilder()
                .append(point.id + " - ")
                .append(String.format("[%.2f, %.2f, %.2f]", point.x, point.y, point.z))
                .color(ChatColor.GREEN)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to get the coordinates in your chat box")))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("%f %f %f", point.x, point.y, point.z)))
                .append(String.format(" in %s", point.worldName), ComponentBuilder.FormatRetention.NONE)
                .create();
            sender.spigot().sendMessage(components);
        }
        Map<String, List<Integer>> groups = Points.getGroups();
        for (String group : groups.keySet()) {
            ComponentBuilder components = new ComponentBuilder()
                .append(group).color(ChatColor.AQUA)
                .append(": ").color(ChatColor.RESET);
            for (int id : groups.get(group))
                components
                    .append(String.format("[%d]",id)).color(ChatColor.GREEN)
                    .append(", ").color(ChatColor.RESET);
            sender.spigot().sendMessage(components.create());
        }
        return CommandResult.SUCCESS;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull CommandSender sender, @NotNull String[] params) {
        if (params.length == 1) return sender instanceof Player
            ? TabResult.of("", "add", "remove", "refresh", "groups")
            : TabResult.EMPTY_RESULT;

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
