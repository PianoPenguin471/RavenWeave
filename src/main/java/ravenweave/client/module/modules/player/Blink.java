package ravenweave.client.module.modules.player;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.weavemc.loader.api.event.ShutdownEvent;
import net.weavemc.loader.api.event.StartGameEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import net.weavemc.loader.api.event.WorldEvent;
import ravenweave.client.event.PacketEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.ComboSetting;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.TickSetting;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Blink extends Module {
    public static ComboSetting<modes> mode;
    public static TickSetting spawnFake;

    public Blink() {
        super("Blink", ModuleCategory.player);
        this.registerSetting(new DescriptionSetting("Chokes packets until disabled."));
        this.registerSetting(mode = new ComboSetting<>("Mode", modes.BOTH));
        this.registerSetting(spawnFake = new TickSetting("Spawn fake player", true));
    }

    private final ArrayList<Packet<?>> outboundPackets = new ArrayList<>(), inboundPackets = new ArrayList<>();
    private static EntityOtherPlayerMP fakePlayer;
    
    @SubscribeEvent
    public void onPacket(PacketEvent e) {
        if (!e.isOutgoing() && (mode.getMode() == modes.BOTH || mode.getMode() == modes.INBOUND)) {
            inboundPackets.add(e.getPacket());
            e.setCancelled(true);
        } else if (e.isOutgoing() && (mode.getMode() == modes.BOTH || mode.getMode() == modes.OUTBOUND)) {
            outboundPackets.add(e.getPacket());
            e.setCancelled(true);
        }
    }
    
    @Override
    public void onEnable() {
        outboundPackets.clear();
        inboundPackets.clear();
        if (spawnFake.isToggled()) {
            if (mc.thePlayer != null) {
                fakePlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
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
            for (Packet<?> packet : outboundPackets) {
                mc.getNetHandler().addToSendQueue(packet);
            }

            outboundPackets.clear();
        }

        if (!inboundPackets.isEmpty()) {
            for (Packet<?> packet : inboundPackets) {
                handleInbound(packet);
            }

            inboundPackets.clear();
        }
    }


    /*
     * I have a better way to do this, however we gatekeep good code around here (works the same in-game though)
     */
    public void handleInbound(Packet<?> packet) {
        Class<?> packetClass = packet.getClass();
        Method[] methods = NetHandlerPlayClient.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getReturnType().equals(void.class) && method.getParameterCount() == 1) {
                if (method.getParameterTypes()[0].equals(packetClass)) {
                    try {
                        method.invoke(mc.getNetHandler(), packet);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onShutdown(ShutdownEvent e) {
        this.disable();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent e) {
        this.disable();
    }

    @SubscribeEvent
    public void onStart(StartGameEvent e) {
        this.disable();
    }

    public enum modes {
        INBOUND, OUTBOUND, BOTH
    }
}
