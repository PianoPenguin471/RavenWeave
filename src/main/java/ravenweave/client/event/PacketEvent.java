package ravenweave.client.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;
import net.weavemc.loader.api.event.CancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public class PacketEvent extends CancellableEvent {
    private Packet<?> packet;
    private boolean outgoing;

    public static class Sent extends PacketEvent {
        public Sent(Packet<?> packet, boolean outgoing) {
            super(packet, true);
        }
    }

    public static class Receive extends PacketEvent {
        public Receive(Packet<?> packet, boolean outgoing) {
            super(packet, false);
        }
    }

}