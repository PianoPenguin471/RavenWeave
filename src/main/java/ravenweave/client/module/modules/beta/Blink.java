package ravenweave.client.module.modules.beta;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.Packet;
import net.weavemc.loader.api.event.ShutdownEvent;
import net.weavemc.loader.api.event.StartGameEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.event.EventDirection;
import ravenweave.client.event.impl.PacketEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.TickSetting;

import java.util.ArrayList;

public class Blink extends Module {

    public static TickSetting inbound, outbound;

    private ArrayList<? extends Packet> outboundPackets = new ArrayList<>();
    private ArrayList<? extends Packet> inboundPackets = new ArrayList<>();

    public Blink() {
        super("Blink", ModuleCategory.beta); // Category: Player
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

    @SubscribeEvent
    public void onDisconnect(ShutdownEvent event) {
        this.disable();
    }

    @SubscribeEvent
    public void onStart(StartGameEvent event) {
        this.disable();
    }


}
