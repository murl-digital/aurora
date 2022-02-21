package digital.murl.aurora.commands;

import digital.murl.aurora.regions.Region;
import digital.murl.aurora.regions.Regions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.CombinedCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;


public class RegionCommand extends CombinedCommand {
    public RegionCommand() {
        super("regions");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull CommandSender sender, @NotNull String[] params) {
        sender.spigot().sendMessage(new ComponentBuilder().append("All regions:").color(ChatColor.AQUA).create());
        for (Region region : Regions.getRegions()) {
            if (region == null) continue;
            BaseComponent[] components = new ComponentBuilder()
                .append(region.id).color(ChatColor.GREEN)
                .append(" - ", ComponentBuilder.FormatRetention.NONE)
                .append(String.format("[%s]", region.type)).color(ChatColor.GREEN)
                .append(String.format(" in %s", region.worldName), ComponentBuilder.FormatRetention.NONE)
                .create();
            sender.spigot().sendMessage(components);
        }
        return CommandResult.SUCCESS;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull CommandSender sender, @NotNull String[] params) {
        if (params.length == 1) return sender instanceof Player
            ? TabResult.of("", "add", "remove", "refresh", "check", "distribute")
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
