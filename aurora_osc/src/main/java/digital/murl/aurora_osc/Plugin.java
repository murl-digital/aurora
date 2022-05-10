package digital.murl.aurora_osc;

import com.illposed.osc.transport.NetworkProtocol;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortInBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Logger;

public final class Plugin extends JavaPlugin {
    public static Plugin plugin;
    public static Logger logger;

    private OSCPortIn receiver;
    private PacketListener listener;

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();

        try {
            receiver = new OSCPortInBuilder()
                    .setLocalPort(3032)
                    .setRemotePort(0)
                    .setNetworkProtocol(NetworkProtocol.UDP)
                    .build();

            listener = new PacketListener();
            receiver.addPacketListener(listener);

            receiver.startListening();
            logger.warning("LISTENING ON PORT 3032");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }
}
