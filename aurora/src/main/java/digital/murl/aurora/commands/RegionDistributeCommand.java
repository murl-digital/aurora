package digital.murl.aurora.commands;

import digital.murl.aurora.regions.Region;
import digital.murl.aurora.regions.Regions;
import digital.murl.aurora.regions.distributors.RegionDistributor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.PlayerCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegionDistributeCommand extends PlayerCommand {

    public RegionDistributeCommand(RegionCommand root) {
        super(root, "distribute");
    }

    @NotNull
    @Override
    public CommandResult onCommand(@NotNull Player sender, @NotNull String[] params) {
        if (params.length < 2) return CommandResult.WRONG_SYNTAX;

        Region region = Regions.getRegion(params[0]);
        if (region == null) return CommandResult.WRONG_SYNTAX;

        RegionDistributor distributor = Regions.getRegionDistributor(params[1]);
        if (distributor == null) return CommandResult.WRONG_SYNTAX;

        for (Location l : distributor.distribute(region, Arrays.copyOfRange(params, 2, params.length)))
            sender.getWorld().spawnParticle(Particle.CLOUD, l, 1, 0,0, 0, 0);

        return CommandResult.SUCCESS;
    }

    @NotNull
    @Override
    public TabResult onTab(@NotNull Player sender, @NotNull String[] params) {
        if (params.length == 1) {
            List<String> regionNames = new ArrayList<>();
            for (Region region : Regions.getRegions())
                regionNames.add(region.id);

            return TabResult.of("", regionNames);
        }

        if (params.length == 2)
            return TabResult.of("", Regions.getRegionDistributors());


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
