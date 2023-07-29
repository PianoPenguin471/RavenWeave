package ravenweave.client.module.modules.beta;

import net.minecraft.network.Packet;
import net.weavemc.loader.api.event.ShutdownEvent;
import net.weavemc.loader.api.event.StartGameEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.event.types.EventDirection;
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
    
    @SubscribeEvent
    public void onPacket(PacketEvent e) {
        if (e.getDirection() == EventDirection.INCOMING) {
            if (!inbound.isToggled()) return;
            inboundPackets.add(e.getPacket());
        } else {
            if (!outbound.isToggled()) return;
            if (!e.getPacket().getClass().getCanonicalName().startsWith("net.minecraft.network.play.client")) return;
            outboundPackets.add(e.getPacket());
        }
        e.setCancelled(true);
    }
    
    @Override
    public void onEnable() {
        outboundPackets.clear();
        inboundPackets.clear();
    }

    @Override
    public void onDisable() {
        for (Packet packet : outboundPackets) {
            System.out.println(packet);
            mc.getNetHandler().addToSendQueue(packet);
        }

        outboundPackets.clear();
        inboundPackets.clear();
    }

    @SubscribeEvent
    public void onDisconnect(ShutdownEvent e) {
        this.disable();
    }

    @SubscribeEvent
    public void onStart(StartGameEvent e) {
        this.disable();
    }


}
