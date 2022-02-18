package digital.murl.aurora;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;
import digital.murl.aurora.commands.*;
import digital.murl.aurora.points.Points;
import digital.murl.aurora.regions.*;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.tozymc.spigot.api.command.CommandController;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Aurora.plugin = this;
        Aurora.logger = getLogger();
        Aurora.logger.info("hello there.");

        PointCommand pointCommand = new PointCommand();
        PointAddCommand pointAddCommand = new PointAddCommand(pointCommand);
        PointRemoveCommand pointRemoveCommand = new PointRemoveCommand(pointCommand);
        PointRefreshCommand pointRefreshCommand = new PointRefreshCommand(pointCommand);

        RegionCommand regionCommand = new RegionCommand();
        RegionAddCommand regionAddCommand = new RegionAddCommand(regionCommand);
        RegionRemoveCommand regionRemoveCommand = new RegionRemoveCommand(regionCommand);
        RegionRefreshCommand regionRefreshCommand = new RegionRefreshCommand(regionCommand);
        RegionCheckCommand regionCheckCommand = new RegionCheckCommand(regionCommand);

        Aurora.commandController = new CommandController(this);

        Aurora.commandController.addCommand(pointCommand);
        Aurora.commandController.addCommand(pointAddCommand);
        Aurora.commandController.addCommand(pointRemoveCommand);
        Aurora.commandController.addCommand(pointRefreshCommand);

        Aurora.commandController.addCommand(regionCommand);
        Aurora.commandController.addCommand(regionAddCommand);
        Aurora.commandController.addCommand(regionRemoveCommand);
        Aurora.commandController.addCommand(regionRefreshCommand);
        Aurora.commandController.addCommand(regionCheckCommand);

        Aurora.registerRegionType("World",
            RegionWorld::jsonConstructor,
            RegionWorld::parameterConstructor,
            RegionWorld::parameterCompleter);
        Aurora.registerRegionType("Sphere",
            RegionSphere::jsonConstructor,
            RegionSphere::parameterConstructor,
            RegionSphere::parameterCompleter);
        Aurora.registerRegionType("Cuboid",
            RegionCuboid::jsonConstructor,
            RegionCuboid::parameterConstructor,
            RegionCuboid::parameterCompleter);

        if (getDataFolder().mkdirs()) {

        }

        Aurora.logger.info("loading points...");
        try {
            Points.load();
        } catch (IOException e) {
            Aurora.logger.severe("Failed to load points: " + e.getMessage());
        }

        Aurora.logger.info("loading regions...");
        try {
            Regions.load();
        } catch (IOException e) {
            Aurora.logger.severe("Failed to load regions: " + e.getMessage());
        }

        Aurora.logger.info("warming up DSL-JSON...");
        try {
            Aurora.dslJson = new DslJson<>(Settings.withRuntime().includeServiceLoader());
            JsonWriter writer = Aurora.dslJson.newWriter();

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

                Aurora.dslJson.serialize(writer, test);
                byte[] buffer = writer.getByteBuffer();
                int size = writer.size();

                HashMap<Object, Object> test2 = (HashMap<Object, Object>) Aurora.dslJson.deserialize(HashMap.class, buffer, size);

                writer.reset();
            }

        } catch (Exception e) {
            Aurora.logger.warning(e.getMessage());
        }

        //Aurora.registerAgent("test", TestAgent.class);
        //Aurora.executeAgentAction("test", "test", new HashMap<>());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
