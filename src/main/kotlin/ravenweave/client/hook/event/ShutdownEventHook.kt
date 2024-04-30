package ravenweave.client.hook.event

import net.weavemc.api.Hook
import net.weavemc.internals.asm
import org.objectweb.asm.tree.ClassNode
import ravenweave.client.callEvent
import ravenweave.client.event.ShutdownEvent
import ravenweave.client.getSingleton
import ravenweave.client.named

/**
 * Corresponds to [ShutdownEvent].
 */
internal class ShutdownEventHook : Hook("net/minecraft/client/Minecraft") {

    /**
     * Inserts a call to
     * [net.minecraft.client.Minecraft.shutdownMinecraftApplet].
     * at the head of [net.minecraft.client.Minecraft.shutdownMinecraftApplet].
     *
     * @see net.minecraft.client.Minecraft.shutdownMinecraftApplet
     */
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.named("shutdownMinecraftApplet").instructions.insert(asm {
            getSingleton<ShutdownEvent>()
            callEvent()
        })
    }
}
