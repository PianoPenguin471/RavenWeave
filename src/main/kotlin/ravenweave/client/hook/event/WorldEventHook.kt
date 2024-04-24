package ravenweave.client.hook.event

import net.weavemc.api.Hook
import net.weavemc.internals.asm
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LabelNode
import ravenweave.client.callEvent
import ravenweave.client.event.WorldEvent
import ravenweave.client.internalNameOf

/**
 * Corresponds to [WorldEvent.Load] and [WorldEvent.Unload].
 */
internal class WorldEventHook: Hook("net/minecraft/client/Minecraft") {

    /**
     * Inserts a call to [WorldEvent.Load] and [WorldEvent.Unload] using the Event Bus.
     *
     * [WorldEvent.Load] is called in the case that [net.minecraft.client.Minecraft.loadWorld] is called,
     * and [net.minecraft.client.multiplayer.WorldClient] is not null.
     *
     * [WorldEvent.Unload] is called in the case that [net.minecraft.client.Minecraft.loadWorld] is called,
     * and [net.minecraft.client.Minecraft.theWorld] is not null.
     *
     * @see net.minecraft.client.Minecraft.loadWorld
     */
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.find {
            it.name == "loadWorld" && it.desc == "(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V"
        }!!.instructions.insert(asm {
            val lbl = LabelNode()

            aload(0)
            getfield("net/minecraft/client/Minecraft", "theWorld", "Lnet/minecraft/client/multiplayer/WorldClient;")
            ifnull(lbl)

            new(internalNameOf<WorldEvent.Unload>())
            dup
            aload(0)
            getfield("net/minecraft/client/Minecraft", "theWorld", "Lnet/minecraft/client/multiplayer/WorldClient;")
            invokespecial(internalNameOf<WorldEvent.Unload>(), "<init>", "(Lnet/minecraft/world/World;)V")
            callEvent()

            +lbl
            f_same()

            val end = LabelNode()
            aload(1)
            ifnull(end)

            new(internalNameOf<WorldEvent.Load>())
            dup
            aload(1)
            invokespecial(internalNameOf<WorldEvent.Load>(), "<init>", "(Lnet/minecraft/world/World;)V")
            callEvent()

            +end
            f_same()
        })
    }
}
