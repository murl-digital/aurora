package fyi.sorenneedscoffee.aurora;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import fyi.sorenneedscoffee.aurora.commands.EffectCmd;
import fyi.sorenneedscoffee.aurora.commands.PointCmd;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.potion.GlobalPotionEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.potion.GlobalPotionEndpoint;
import fyi.sorenneedscoffee.aurora.http.models.potion.GlobalPotionModel;
import fyi.sorenneedscoffee.aurora.http.providers.CORSFilter;
import fyi.sorenneedscoffee.aurora.http.providers.GeneralExceptionMapper;
import fyi.sorenneedscoffee.aurora.http.providers.GsonProvider;
import fyi.sorenneedscoffee.aurora.util.DataManager;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.PointUtil;
import fyi.sorenneedscoffee.aurora.util.annotation.StaticAction;
import fyi.sorenneedscoffee.aurora.util.annotation.StaticEffect;
import fyi.sorenneedscoffee.aurora.util.annotation.StaticModel;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URI;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class Aurora extends JavaPlugin {
    public static Aurora plugin;
    public static Logger logger;
    public static FileConfiguration config;

    public static ProtocolManager protocolManager;
    public static PointUtil pointUtil;
    public static DataManager dataManager;
    public static Gson gson;

    public static HttpServer httpServer;
    public static ExecutorService httpExecutor;

    @Override
    public void onEnable() {
        plugin = this;
        logger = this.getLogger();
        gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        if (plugin.getDataFolder().mkdirs()) {
            if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
                plugin.saveDefaultConfig();
            }
        }

        config = plugin.getConfig();

        protocolManager = ProtocolLibrary.getProtocolManager();
        dataManager = new DataManager(plugin);
        pointUtil = new PointUtil().load();

        Reflections reflections = new Reflections("fyi.sorenneedscoffee.aurora.http",
                new TypeAnnotationsScanner(),
                new MethodAnnotationsScanner(),
                new SubTypesScanner()
        );
        Set<Class<? extends Endpoint>> endpoints = reflections.getSubTypesOf(Endpoint.class);

        if (config.getBoolean("remote.enabled")) {
            String hostname = config.getString("remote.httpHostname");
            int port = config.getInt("remote.httpPort");

            try {
                URI base = UriBuilder.fromUri("http://" +
                        Objects.requireNonNull(hostname.equals("auto") ? InetAddress.getLocalHost().getHostAddress() : hostname)
                        + "/").port(port).build();

                logger.info("Starting HTTP Server at " + base + "...");

                ResourceConfig resourceConfig = new ResourceConfig();
                resourceConfig.register(GsonProvider.class);
                resourceConfig.register(GeneralExceptionMapper.class);
                resourceConfig.register(CORSFilter.class);
                resourceConfig.register(OpenApiResource.class);
                for (Class<? extends Endpoint> e : endpoints) {
                    resourceConfig.register(e);
                }

                httpServer = JdkHttpServerFactory.createHttpServer(base, resourceConfig, false);
                httpExecutor = new ThreadPoolExecutor(2, Integer.MAX_VALUE,
                        60L, TimeUnit.SECONDS,
                        new SynchronousQueue<>());
                httpServer.setExecutor(httpExecutor);
                httpServer.start();

                if (config.getBoolean("remote.solarflare.enabled")) {
                    logger.info("Registering with SolarFlare...");
                    String host = Objects.equals(config.getString("remote.solarflare.providedHostname"), "auto") ? base.getHost() : config.getString("remote.solarflare.providedHostname");

                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = RequestBody.create(
                            host + ":" + base.getPort(),
                            MediaType.parse(host + ":" + base.getPort()));
                    Request request = new Request.Builder()
                            .url(Objects.requireNonNull(config.getString("remote.solarflare.url")))
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
        this.getCommand("effects").setExecutor(new EffectCmd());

        try {
            logger.info("Starting static effects..");
            File staticEffectsFile = new File(plugin.getDataFolder(), "staicEffects.json");
            if (!staticEffectsFile.exists()) {
                gson.toJson(new JsonArray(), new FileWriter(staticEffectsFile));
            }

            JsonArray effectArray = (JsonArray) new JsonParser().parse(new FileReader(staticEffectsFile));

            for (JsonElement el : effectArray) {
                if (!(el instanceof JsonObject))
                    continue;

                JsonObject obj = (JsonObject) el;

                for (Class<? extends Endpoint> e : endpoints) {
                    StaticEffect[] a = e.getAnnotationsByType(StaticEffect.class);
                    if (a.length != 0 && a[0].value().equals(obj.get("effect").getAsString())) {
                        Object model = null;
                        for (Class<?> cl : reflections.getTypesAnnotatedWith(StaticModel.class)) {
                            if (cl.getAnnotation(StaticModel.class).value().equals(obj.get("effect").getAsString()))
                                model = gson.fromJson(obj.get("model"), Array.newInstance(cl, 1).getClass());
                        }
                        Object finalModel = model;
                        reflections.getMethodsAnnotatedWith(StaticAction.class).forEach(m -> {
                            try {
                                m.invoke(e.getDeclaredConstructor().newInstance(), finalModel);
                            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
                                logger.warning("The plugin tried to initialize the static");
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            logger.warning("An error occurred while starting a static effect. It will not be active.");
            logger.warning(ExceptionUtils.getMessage(e));
            logger.warning(ExceptionUtils.getStackTrace(e));
        }

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
        Aurora.logger.info("Stopping currently active effects...");
        EffectManager.stopAll(true);
        if (httpServer != null) {
            Aurora.logger.info("Shutting down HTTP server...");
            httpServer.stop(0);
            httpExecutor.shutdown();
        }
    }
}
