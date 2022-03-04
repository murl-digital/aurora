package digital.murl.aurora.commands.regions;

import digital.murl.aurora.regions.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.PlayerCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

import java.util.ArrayList;
import java.util.List;

public class RegionRemoveCommand extends PlayerCommand {

    public RegionRemoveCommand(RegionCommand root) {
        super(root, "remove");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull Player sender, @NotNull String[] params) {
        if (params.length == 0)
            return CommandResult.WRONG_SYNTAX;

        for (String region : params)
            Regions.removeRegion(region);

        Regions.save();

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
