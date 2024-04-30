package ravenweave.client.hook.event

import net.weavemc.api.Hook
import net.weavemc.internals.asm
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode
import ravenweave.client.callEvent
import ravenweave.client.event.RenderWorldEvent
import ravenweave.client.internalNameOf
import ravenweave.client.named

/**
 * Corresponds to [RenderWorldEvent].
 */
internal class RenderWorldEventHook : Hook("net/minecraft/client/renderer/EntityRenderer") {

    /**
     * Inserts a call to [RenderWorldEvent]'s constructor at the head of
     * [net.minecraft.client.renderer.EntityRenderer.renderWorldPass], which
     * is called in the event of any world render.
     */
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        val mn = node.methods.named("renderWorldPass")

        mn.instructions.insertBefore(
            mn.instructions.find { it is LdcInsnNode && it.cst == "hand" },
            asm {
                new(internalNameOf<RenderWorldEvent>())
                dup
                fload(2)
                invokespecial(internalNameOf<RenderWorldEvent>(), "<init>", "(F)V")
                callEvent()
            }
        )
    }
}
