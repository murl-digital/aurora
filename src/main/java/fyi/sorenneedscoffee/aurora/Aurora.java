package fyi.sorenneedscoffee.aurora;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import fyi.sorenneedscoffee.aurora.commands.PointCmd;
import fyi.sorenneedscoffee.aurora.effects.bossbar.BossBarEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.DragonEndpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.GlobalPotionEndpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.ParticleEndpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.StopEndpoint;
import fyi.sorenneedscoffee.aurora.util.DataManager;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.EntityHider;
import fyi.sorenneedscoffee.aurora.util.PointUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.reflections.Reflections;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public final class Aurora extends JavaPlugin {
    public static Aurora plugin;
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

            URI base = null;
            try {
                base = UriBuilder.fromUri("http://" +
                        Objects.requireNonNull(hostname.equals("auto") ? InetAddress.getLocalHost().getHostAddress() : hostname)
                    + "/").port(port).build();
            } catch (UnknownHostException e) {
                logger.severe(e.getMessage());
            }

            try {
                logger.info("Starting HTTP Server at "+base+"...");
                Reflections reflections = new Reflections("fyi.sorenneedscoffee.aurora.http.endpoints");
                Set<Class<? extends Endpoint>> subTypes = reflections.getSubTypesOf(Endpoint.class);
                ResourceConfig resourceConfig = new ResourceConfig();
                for (Class<? extends Endpoint> e : subTypes) {
                    resourceConfig.register(e);
                }
                httpServer = JdkHttpServerFactory.createHttpServer(base, resourceConfig, false);

                httpExecutor = Executors.newCachedThreadPool();
                httpServer.setExecutor(httpExecutor);

                httpServer.start();

                if (config.getBoolean("remote.solarwind.enabled")) {
                    logger.info("Registering with SolarWind...");
                    String providedHostname = config.getString("remote.solarwind.providedHostname");

                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = RequestBody.create((providedHostname.equals("auto") ? base.getHost() : providedHostname) + ":" + base.getPort(), MediaType.parse(base.getHost() + ":" + base.getPort()));
                    Request request = new Request.Builder()
                            .url(Objects.requireNonNull(config.getString("remote.solarwind.url")))
                            .post(body)
                            .build();
                    client.newCall(request).execute();
                }
            } catch (Exception e) {
                logger.severe(ExceptionUtils.getMessage(e));
                logger.severe(ExceptionUtils.getStackTrace(e));
            }
        }

        this.getCommand("point").setExecutor(new PointCmd());
        Bukkit.getLogger().info("\n" +
                "_______                                   \n" +
                "___    |___  ___________________________ _\n" +
                "__  /| |  / / /_  ___/  __ \\_  ___/  __ `/\n" +
                "_  ___ / /_/ /_  /   / /_/ /  /   / /_/ / \n" +
                "/_/  |_\\__,_/ /_/    \\____//_/    \\__,_/  \n" +
                "                                          \n"
        );
        Bukkit.getLogger().info("Oooo, pretty lights");
    }

    @Override
    public void onDisable() {
        Aurora.logger.info("Shutting down HTTP server...");
        httpServer.stop(0);
        httpExecutor.shutdown();
        Aurora.logger.info("Stopping currently active effects...");
        EffectManager.stopAll();
    }
}