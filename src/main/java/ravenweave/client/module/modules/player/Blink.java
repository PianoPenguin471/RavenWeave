package ravenweave.client.module.modules.player;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.weavemc.api.event.SubscribeEvent;
import ravenweave.client.event.PacketEvent;
import ravenweave.client.event.WorldEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.ComboSetting;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.TickSetting;

import java.util.ArrayList;

public class Blink extends Module {
    public static ComboSetting<modes> mode;
    public static TickSetting spawnFake;

    private final ArrayList<Packet<INetHandlerPlayServer>> outboundPackets = new ArrayList<>();
    private final ArrayList<Packet<INetHandlerPlayClient>> inboundPackets = new ArrayList<>();
    public static EntityOtherPlayerMP fakePlayer;

    public Blink() {
        super("Blink", ModuleCategory.player);
        this.registerSetting(new DescriptionSetting("Chokes packets until disabled."));
        this.registerSetting(mode = new ComboSetting<>("Mode", modes.BOTH));
        this.registerSetting(spawnFake = new TickSetting("Spawn fake player", true));
    }
    
    @SubscribeEvent
    public void onPacket(PacketEvent e) {
        if (e instanceof PacketEvent.Receive && (mode.getMode() == modes.BOTH || mode.getMode() == modes.INBOUND)) {
            // The below is a bad approach but instanceof returns errors. (Sorry niki & other good devs)
            if (e.getPacket().getClass().getCanonicalName().startsWith("net.minecraft.network.play.server")) {
                inboundPackets.add((Packet<INetHandlerPlayClient>) e.getPacket());
                e.setCancelled(true);
            }
        }

        if (e instanceof PacketEvent.Send && (mode.getMode() == modes.BOTH || mode.getMode() == modes.OUTBOUND)) {
            // The below is a bad approach but instanceof returns errors. (Sorry niki & other good devs)
            if (e.getPacket().getClass().getCanonicalName().startsWith("net.minecraft.network.play.server")) {
                outboundPackets.add((Packet<INetHandlerPlayServer>) e.getPacket());
                e.setCancelled(true);
            }
        }
    }
    
    @Override
    public void onEnable() {
        outboundPackets.clear();
        inboundPackets.clear();
        if (spawnFake.isToggled()) {
            if (mc.thePlayer != null) {
                fakePlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
                fakePlayer.setRotationYawHead(mc.thePlayer.rotationYawHead);
                fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
                mc.theWorld.addEntityToWorld(fakePlayer.getEntityId(), fakePlayer);
            }
        }
    }

    @Override
    public void onDisable() {
        if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
            fakePlayer = null;
        }

        if (!outboundPackets.isEmpty()) {
            for (Packet<INetHandlerPlayServer> packet : outboundPackets) {
                mc.getNetHandler().addToSendQueue(packet);
            }

            outboundPackets.clear();
        }

        if (!inboundPackets.isEmpty()) {
            for (Packet<INetHandlerPlayClient> packet : inboundPackets) {
                packet.processPacket(mc.getNetHandler());
            }

            inboundPackets.clear();
        }
    }

    @SubscribeEvent
    public void onShutdown(ravenweave.client.event.ShutdownEvent e) {
        this.disable();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent e) {
        this.disable();
    }

    @SubscribeEvent
    public void onStart(ravenweave.client.event.StartGameEvent e) {
        this.disable();
    }

    public enum modes {
        INBOUND, OUTBOUND, BOTH
    }
}
