package ravenweave.client.hook.event

import net.weavemc.api.Hook
import net.weavemc.internals.asm
import org.objectweb.asm.tree.ClassNode
import ravenweave.client.callEvent
import ravenweave.client.event.ServerConnectEvent
import ravenweave.client.internalNameOf
import ravenweave.client.named

/**
 * Corresponds to [ServerConnectEvent].
 */
internal class ServerConnectEventHook : Hook("net/minecraft/client/multiplayer/GuiConnecting") {

    /**
     * Inserts a call to [ServerConnectEvent] at the head of [net.minecraft.client.multiplayer.GuiConnecting.connect].
     */
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.named("connect").instructions.insert(asm {
            new(internalNameOf<ServerConnectEvent>())
            dup
            aload(1)
            iload(2)
            invokespecial(
                internalNameOf<ServerConnectEvent>(),
                "<init>",
                "(Ljava/lang/String;I)V"
            )

            callEvent()
        })
    }
}
