package fyi.sorenneedscoffee.eyecandy;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import fyi.sorenneedscoffee.eyecandy.commands.PointCmd;
import fyi.sorenneedscoffee.eyecandy.http.endpoints.*;
import fyi.sorenneedscoffee.eyecandy.util.DataManager;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;
import fyi.sorenneedscoffee.eyecandy.util.EntityHider;
import fyi.sorenneedscoffee.eyecandy.util.PointUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public final class EyeCandy extends JavaPlugin {
    public static EyeCandy plugin;
    public static Logger logger;
    public static FileConfiguration config;

    public static ProtocolManager protocolManager;
    public static PointUtil pointUtil;
    public static DataManager dataManager;
    public static EntityHider hider;
    public static Gson gson;

    public static HttpServer httpServer;
    public static ExecutorService httpExecutor;

    @Override
    public void onEnable() {
        plugin = this;
        logger = this.getLogger();
        gson = new Gson();

        if (plugin.getDataFolder().mkdirs()) {
            if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
                plugin.saveDefaultConfig();
            }
        }

        config = plugin.getConfig();

        hider = new EntityHider(this, EntityHider.Policy.BLACKLIST);
        protocolManager = ProtocolLibrary.getProtocolManager();
        dataManager = new DataManager(plugin);
        pointUtil = new PointUtil().load();

        if (config.getBoolean("remote.enabled")) {
            String hostname = config.getString("remote.httpHostname");
            int port = config.getInt("remote.httpPort");

            try {
                logger.info("Starting HTTP Server...");
                URI base = UriBuilder.fromUri("http://"+Objects.requireNonNull(hostname)+"/").port(port).build();
                ResourceConfig resourceConfig = new ResourceConfig().packages("fyi.sorenneedscoffee.eyecandy.http.endpoints");
                httpServer = JdkHttpServerFactory.createHttpServer(base, resourceConfig, false);

                httpExecutor = Executors.newCachedThreadPool();
                httpServer.setExecutor(httpExecutor);

                httpServer.start();
            } catch (NullPointerException e) {
                logger.severe(ExceptionUtils.getMessage(e));
                logger.severe(ExceptionUtils.getStackTrace(e));
            }
        }

        this.getCommand("point").setExecutor(new PointCmd());
        Bukkit.getLogger().info("\n" +
                "__________               _________                _________         \n" +
                "___  ____/_____  _______ __  ____/______ ________ ______  /_____  __\n" +
                "__  __/   __  / / /_  _ \\_  /     _  __ `/__  __ \\_  __  / __  / / /\n" +
                "_  /___   _  /_/ / /  __// /___   / /_/ / _  / / // /_/ /  _  /_/ / \n" +
                "/_____/   _\\__, /  \\___/ \\____/   \\__,_/  /_/ /_/ \\__,_/   _\\__, /  \n" +
                "          /____/                                           /____/   \n"
        );
        Bukkit.getLogger().info("Made fresh every day for your eyeholes");
    }

    @Override
    public void onDisable() {
        EyeCandy.logger.info("Shutting down HTTP server...");
        httpServer.stop(0);
        httpExecutor.shutdown();
        EyeCandy.logger.info("Stopping currently active effects...");
        EffectManager.stopAll();
    }
}
