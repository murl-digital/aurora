package digital.murl.aurora.regions.distributors;

import digital.murl.aurora.regions.Region;
import org.bukkit.Location;

@FunctionalInterface
public interface RegionDistributor {
    Location[] distribute(Region region, String[] settings);
}
