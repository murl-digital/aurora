package digital.murl.aurora.regions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class RegionWorld extends Region {
    public RegionWorld(String id, String worldName) {
        super(id, worldName, "World");
    }

    @Override
    public boolean collisionCheck(Location location) {
        return worldName.equals(location.getWorld().getName());
    }

    @Override
    public void populateMap(Map<String, Object> data) {

    }

    public static Region mapConstructor(Map<String, Object> data) {
        String id = (String)data.get("id");
        String world = (String)data.get("world");
        return new RegionWorld(id, world);
    }

    public static RegionParameterConstructor.Result parameterConstructor(Player sender, String[] params) {
        if (params.length != 2)
            return RegionParameterConstructor.Result.WRONG_SYNTAX;

        Regions.addRegion(new RegionWorld(params[0], sender.getLocation().getWorld().getName()));
        Regions.save();

        return RegionParameterConstructor.Result.SUCCESS;
    }

    public static String[] parameterCompleter(Player sender, String[] params) {
        return null;
    }
}
