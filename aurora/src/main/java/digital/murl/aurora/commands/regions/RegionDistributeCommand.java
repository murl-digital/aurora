package digital.murl.aurora.commands.regions;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import digital.murl.aurora.Plugin;
import digital.murl.aurora.regions.Region;
import digital.murl.aurora.regions.RegionRegistrar;
import digital.murl.aurora.regions.Regions;
import digital.murl.aurora.regions.distributors.Distributor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.tozymc.spigot.api.command.PlayerCommand;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;
import xyz.tozymc.spigot.api.util.bukkit.permission.PermissionWrapper;

import java.util.*;
import java.util.logging.Level;

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

        Map<String, Object> distributorParams = new HashMap<>();

        String rawJson = String.join(" ", Arrays.copyOfRange(params, 2, params.length));
        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(rawJson, JsonObject.class);
            distributorParams = gson.fromJson(json, HashMap.class);
        } catch (Exception e) {
            Plugin.logger.log(Level.WARNING, "Could not deserialize distributor json payload: ", e);
            Plugin.logger.log(Level.WARNING, "JSON: " + rawJson);
        }

        Distributor distributor = null;

        try {
            distributor = RegionRegistrar.getRegionDistributor(params[1], region, distributorParams);
        } catch (Exception e) {
            Plugin.logger.log(Level.WARNING, "Problem initializing distributor: ", e);
        }

        if (distributor == null) return CommandResult.WRONG_SYNTAX;

        for (Location l : distributor.distribute())
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
            return TabResult.of("", RegionRegistrar.getRegionDistributors());


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
