package digital.murl.aurora.commands.regions;

import digital.murl.aurora.regions.Region;
import digital.murl.aurora.regions.Regions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.PlayerCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

import java.util.ArrayList;
import java.util.List;

public class RegionCheckCommand extends PlayerCommand {

    public RegionCheckCommand(RegionCommand root) {
        super(root, "check");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull Player sender, @NotNull String[] params) {
        if (params.length == 0) {
            for (Region region : Regions.getRegions()) {
                if (region.collisionCheck(sender.getLocation()))
                    sender.spigot().sendMessage(new ComponentBuilder().append(region.id).color(ChatColor.AQUA).create());
            }
        } else {
            for (String param : params) {
                Region region = Regions.getRegion(param);
                if (region == null) continue;
                ComponentBuilder builder = new ComponentBuilder()
                    .append(region.id).color(ChatColor.AQUA)
                    .append(" - ", ComponentBuilder.FormatRetention.NONE);
                if (region.collisionCheck(sender.getLocation()))
                    builder.append("True").color(ChatColor.GREEN);
                else builder.append("False").color(ChatColor.RED);
                sender.spigot().sendMessage(builder.create());
            }
        }
        return CommandResult.SUCCESS;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull Player sender, @NotNull String[] params) {
        if (params.length == 0)
            return TabResult.EMPTY_RESULT;

        List<String> regionNames = new ArrayList<>();
        for (Region region : Regions.getRegions())
            regionNames.add(region.id);

        return TabResult.of("", regionNames);
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
