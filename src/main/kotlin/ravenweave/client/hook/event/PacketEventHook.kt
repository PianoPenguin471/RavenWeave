package ravenweave.client.hook.event

import net.weavemc.api.Hook
import net.weavemc.api.event.CancellableEvent
import net.weavemc.internals.asm
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LabelNode
import ravenweave.client.callEvent
import ravenweave.client.event.PacketEvent
import ravenweave.client.internalNameOf
import ravenweave.client.named

internal class PacketEventHook: Hook("net/minecraft/network/NetworkManager") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.filter { it.name == "sendPacket" }.forEach {
            it.instructions.insert(asm {
                new(internalNameOf<PacketEvent.Send>())
                dup; dup
                aload(1)
                invokespecial(internalNameOf<PacketEvent.Send>(), "<init>", "(Lnet/minecraft/network/Packet;)V")
                callEvent()

                val end = LabelNode()
                dup
                invokevirtual(internalNameOf<CancellableEvent>(), "isCancelled", "()Z")
                ifeq(end)
                pop
                _return
                +end
                invokevirtual(internalNameOf<PacketEvent.Send>(), "getPacket", "()Lnet/minecraft/network/Packet;")
                astore(1)
                f_same()
            })
        }

        node.methods.named("channelRead0").instructions.insert(asm {
            new(internalNameOf<PacketEvent.Receive>())
            dup; dup
            aload(2)
            invokespecial(internalNameOf<PacketEvent.Receive>(), "<init>", "(Lnet/minecraft/network/Packet;)V")
            callEvent()

            val end = LabelNode()
            dup
            invokevirtual(internalNameOf<CancellableEvent>(), "isCancelled", "()Z")
            ifeq(end)
            pop
            _return
            +end
            invokevirtual(internalNameOf<PacketEvent.Receive>(), "getPacket", "()Lnet/minecraft/network/Packet;")
            astore(2)
            f_same()
        })

        cfg.computeFrames()
    }
}
