package fyi.sorenneedscoffee.eyecandy;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sun.net.httpserver.HttpServer;
import fyi.sorenneedscoffee.eyecandy.commands.PointCmd;
import fyi.sorenneedscoffee.eyecandy.effects.implementations.DragonEffect;
import fyi.sorenneedscoffee.eyecandy.http.DragonHandler;
import fyi.sorenneedscoffee.eyecandy.http.TestHandler;
import fyi.sorenneedscoffee.eyecandy.util.DataManager;
import fyi.sorenneedscoffee.eyecandy.util.EntityHider;
import fyi.sorenneedscoffee.eyecandy.util.PointUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

public final class EyeCandy extends JavaPlugin {
    public static EntityHider hider;
    public static EyeCandy plugin;
    public static ProtocolManager protocolManager;
    public static PointUtil pointUtil;
    public static DataManager dataManager;
    public static HttpServer httpServer;
    public static Logger logger;

    @Override
    public void onEnable() {
        plugin = this;
        logger = this.getLogger();
        hider = new EntityHider(this, EntityHider.Policy.BLACKLIST);
        protocolManager = ProtocolLibrary.getProtocolManager();
        dataManager = new DataManager(plugin);
        pointUtil = new PointUtil().load();

        try {
            logger.info("Starting HTTP Server at " + InetAddress.getLocalHost().getHostAddress() +"...");
            httpServer = HttpServer.create(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 8001), 0);
            httpServer.createContext("/test", new TestHandler());
            httpServer.createContext("/effects/dragon", new DragonHandler());
            httpServer.start();
        } catch (IOException e) {
            logger.severe(ExceptionUtils.getMessage(e));
            logger.severe(ExceptionUtils.getStackTrace(e));
        }

        this.getCommand("point").setExecutor(new PointCmd());
    }

    @Override
    public void onDisable() {
        httpServer.stop(0);
    }
}
