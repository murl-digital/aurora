package digital.murl.aurora.regions;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.tozymc.spigot.api.command.result.CommandResult;
import xyz.tozymc.spigot.api.command.result.TabResult;

public class RegionWorld extends Region {
    public RegionWorld(String id, String worldName) {
        super(id, worldName, "World");
    }

    @Override
    public boolean collisionCheck(Location location) {
        return worldName.equals(location.getWorld().getName());
    }

    @Override
    public void populateJsonObject(JsonObject object) {

    }

    public static Region jsonConstructor(JsonObject json) {
        String id = json.get("id").getAsString();
        String world = json.get("world").getAsString();
        return new RegionWorld(id, world);
    }

    public static CommandResult parameterConstructor(Player sender, String[] params) {
        if (params.length != 2)
            return CommandResult.WRONG_SYNTAX;

        Regions.addRegion(new RegionWorld(params[0], sender.getLocation().getWorld().getName()));
        Regions.save();

        return CommandResult.SUCCESS;
    }

    public static TabResult parameterCompleter(Player sender, String[] params) {
        return TabResult.EMPTY_RESULT;
    }
}
