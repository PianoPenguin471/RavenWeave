package ravenweave.client.module.modules.beta;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.weavemc.loader.api.event.PacketEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FakeLag extends Module {
    public List<TimedPacket> inbound = new ArrayList<>();
    public List<TimedPacket> outbound = new ArrayList<>();
    public static SliderSetting inboundDelay, outboundDelay;
    public FakeLag() {
        super("FakeLag", ModuleCategory.beta);
        this.registerSetting(new DescriptionSetting("Do not use. This will crash your game for no reason"));
        this.registerSetting(inboundDelay = new SliderSetting("Inbound Delay", 500, 0, 1000, 10));
        this.registerSetting(outboundDelay = new SliderSetting("Outbound Delay", 500, 0, 1000, 10));
    }

    @Override
    public void onEnable() {
        inbound.clear();
        outbound.clear();
    }

    @Override
    public void onDisable() {
        outbound.forEach(timedPacket -> mc.getNetHandler().addToSendQueue(timedPacket.packet));
        outbound.clear();
        inbound.forEach(timedPacket -> handleInbound(timedPacket.packet));
        inbound.clear();
    }
    @SubscribeEvent
    public void onPacketInbound(PacketEvent.Receive event) {
        inbound.add(new TimedPacket(event.getPacket()));
        event.setCancelled(true);

        for (TimedPacket timedPacket: inbound) {
            if (timedPacket.time + inboundDelay.getInput() <= System.currentTimeMillis()) {
                handleInbound(timedPacket.packet);
                inbound.remove(timedPacket);
            }
        }
    }

    @SubscribeEvent
    public void onPacketOutBound(PacketEvent.Send event) {
        outbound.add(new TimedPacket(event.getPacket()));
        event.setCancelled(true);

        for (TimedPacket timedPacket: outbound) {
            if (timedPacket.time + outboundDelay.getInput() <= System.currentTimeMillis()) {
                mc.getNetHandler().addToSendQueue(timedPacket.packet);
                outbound.remove(timedPacket);
            }
        }

    }

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

    public static class TimedPacket {
        Packet packet;
        long time;
        public TimedPacket(Packet packet) {
            this.packet = packet;
            this.time = System.currentTimeMillis();
        }
    }
}
