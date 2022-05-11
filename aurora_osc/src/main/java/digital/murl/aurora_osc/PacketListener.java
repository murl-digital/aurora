package digital.murl.aurora_osc;

import com.illposed.osc.*;
import digital.murl.aurora.agents.Agents;
import digital.murl.aurora.effects.Effects;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static digital.murl.aurora_osc.Plugin.logger;

public class PacketListener implements OSCPacketListener {
    @Override
    public void handlePacket(final OSCPacketEvent event) {
        try {
            OSCMessage msg = (OSCMessage)event.getPacket();
            handleMessage(msg);
        } catch (Exception e1) {
            try {
                OSCBundle bundle = (OSCBundle)event.getPacket();
                for (OSCPacket packet : bundle.getPackets()) {
                    OSCMessage msg = (OSCMessage)packet;
                    handleMessage(msg);
                }
            } catch (Exception e2) {
                logger.warning("SOMETHING UNEXPECTED HAPPENED\nSTACKTRACE 1: " + e1.getMessage() + "\nSTACKTRACE 2" + e2.getMessage());
            }
        }
    }

    @Override
    public void handleBadData(final OSCBadDataEvent event) {}

    private void handleMessage(final OSCMessage msg) {
        String[] address = msg.getAddress().split("/");
        if (!address[1].equals("Aurora")) return;

        logger.log(Level.INFO, msg.getAddress() + ": " + msg.getInfo().getArgumentTypeTags() + msg.getInfo().getArgumentTypeTags().length());

        double v;
        Object value = 0;

        switch (msg.getInfo().getArgumentTypeTags().charAt(0)) {
            case 'i':
                v = (int)(msg.getArguments().get(0));
                value = v;
                break;
            case 'f':
                v = (float)(msg.getArguments().get(0));
                value = v;
                break;
            case 'd':
                v = (double)(msg.getArguments().get(0));
                value = v;
                break;
            case 's':
                value = (String)(msg.getArguments().get(0));
                break;
        }

        logger.log(Level.INFO, msg.getAddress() + ": " + value);

        if (address[2].equals("Effects")) {
            Map<String, Object> map = new HashMap<>();
            map.put(address[6], value);
            Effects.executeEffectAction(address[3], address[4], address[5], map);
        }

        if (address[2].equals("Agents")) {
            Map<String, Object> map = new HashMap<>();
            map.put(address[5], value);
            Agents.executeAgentAction(address[3], address[4], map);
        }
    }
}
