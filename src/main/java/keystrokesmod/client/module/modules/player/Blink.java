package keystrokesmod.client.module.modules.player;

import java.util.ArrayList;

import com.google.common.eventbus.Subscribe;

import keystrokesmod.client.event.EventDirection;
import keystrokesmod.client.event.impl.PacketEvent;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.TickSetting;
import net.minecraft.network.Packet;

public class Blink extends Module {

    public static TickSetting inbound, outbound;

    private ArrayList<? extends Packet> outboundPackets = new ArrayList<>();
    private ArrayList<? extends Packet> inboundPackets = new ArrayList<>();

    public Blink() {
        super("Blink", /*ModuleCategory.player*/ModuleCategory.beta);
        this.registerSetting(inbound = new TickSetting("Block Inbound", true));
        this.registerSetting(outbound = new TickSetting("Block Outbound", true));
    }
    
    @Subscribe
    public void packetEvent(PacketEvent p) {
        if (p.getDirection() == EventDirection.INCOMING) {
            if (!inbound.isToggled()) return;
            inboundPackets.add(p.getPacket());
        } else {
            if (!outbound.isToggled()) return;
            outboundPackets.add(p.getPacket());
        }

        p.setCancelled(true);
    }
    
    @Override
    public void onEnable() {
        outboundPackets.clear();
        inboundPackets.clear();
    }
    
    @Override
    public void onDisable() {
        for (Packet packet : outboundPackets) {
            mc.getNetHandler().addToSendQueue(packet);
        }

        outboundPackets.clear();
        inboundPackets.clear();
    }
}
