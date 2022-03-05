package digital.murl.aurora;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;
import digital.murl.aurora.commands.*;
import digital.murl.aurora.commands.points.*;
import digital.murl.aurora.commands.regions.*;
import digital.murl.aurora.effects.EffectManager;
import digital.murl.aurora.points.Points;
import digital.murl.aurora.regions.*;
import digital.murl.aurora.regions.distributors.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.tozymc.spigot.api.command.CommandController;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Plugin extends JavaPlugin {
    public static Logger logger;
    public static Plugin plugin;
    public static FileConfiguration config;
    public static DslJson<Object> dslJson;

    static CommandController commandController;

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();
        logger.info("hello there.");

        PointCommand pointCommand = new PointCommand();
        RegionCommand regionCommand = new RegionCommand();

        commandController = new CommandController(this);

        commandController.addCommand(pointCommand);
        commandController.addCommand(new PointAddCommand(pointCommand));
        commandController.addCommand(new PointRemoveCommand(pointCommand));
        commandController.addCommand(new PointRefreshCommand(pointCommand));
        commandController.addCommand(new PointGroupsCommand(pointCommand));

        commandController.addCommand(regionCommand);
        commandController.addCommand(new RegionAddCommand(regionCommand));
        commandController.addCommand(new RegionRemoveCommand(regionCommand));
        commandController.addCommand(new RegionRefreshCommand(regionCommand));
        commandController.addCommand(new RegionCheckCommand(regionCommand));
        commandController.addCommand(new RegionDistributeCommand(regionCommand));

        commandController.addCommand(new AgentCommand());
        commandController.addCommand(new EffectCommand());

        Aurora.registerRegionType("World",
            RegionWorld::mapConstructor,
            RegionWorld::parameterConstructor,
            RegionWorld::parameterCompleter);
        Aurora.registerRegionType("Sphere",
            RegionSphere::mapConstructor,
            RegionSphere::parameterConstructor,
            RegionSphere::parameterCompleter);
        Aurora.registerRegionType("Cuboid",
            RegionCuboid::mapConstructor,
            RegionCuboid::parameterConstructor,
            RegionCuboid::parameterCompleter);

        Aurora.registerRegionDistributor("CuboidFillGrid", CuboidDistributor::fillGrid);
        Aurora.registerRegionDistributor("CuboidSurfaceGrid", CuboidDistributor::surfaceGrid);
        Aurora.registerRegionDistributor("SphereSurfaceFibonacci", SphereDistributor::surfaceFibonacci);

        if (getDataFolder().mkdirs()) {
            logger.info("creating default config...");
            saveDefaultConfig();
        }

        config = getConfig();

        logger.info("loading points...");
        try {
            Points.load();
        } catch (IOException e) {
            logger.severe("Failed to load points: " + e.getMessage());
        }

        logger.info("loading regions...");
        try {
            Regions.load();
        } catch (IOException e) {
            logger.severe("Failed to load regions: " + e.getMessage());
        }

        logger.info("warming up DSL-JSON...");
        try {
            dslJson = new DslJson<>(Settings.withRuntime().includeServiceLoader());
            JsonWriter writer = dslJson.newWriter();

            for (int i = 0; i < 10000; i++) {
                HashMap<Object, Object> test = new HashMap<>();
                test.put("test", 0);
                test.put("data", Stream.of(new Object[][]{
                    {"testValue", 69},
                    {"rocks", "lots of them"},
                    {"array", new int[]{1, 2, 3, 4, 5, 6, 7, 8}}
                }).collect(Collectors.toMap(k -> k[0], v -> v[1])));
                if (i % 2 == 0)
                    test.put("sneaky bastard", 69);

                dslJson.serialize(writer, test);
                byte[] buffer = writer.getByteBuffer();
                int size = writer.size();

                HashMap<Object, Object> test2 = (HashMap<Object, Object>) dslJson.deserialize(HashMap.class, buffer, size);

                writer.reset();
            }

        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        EffectManager.shutDown();
    }
}
