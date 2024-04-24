package ravenweave.client.hook.event

import net.weavemc.api.Hook
import net.weavemc.internals.asm
import org.objectweb.asm.Opcodes.RETURN
import org.objectweb.asm.tree.ClassNode
import ravenweave.client.callEvent
import ravenweave.client.event.RenderGameOverlayEvent
import ravenweave.client.internalNameOf
import ravenweave.client.named

internal class RenderGameOverlayHook : Hook(
    "net/minecraft/client/gui/GuiIngame",
    "net/minecraftforge/client/GuiIngameForge"
) {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        val mn = node.methods.named("renderGameOverlay")

        mn.instructions.insert(asm {
            new(internalNameOf<RenderGameOverlayEvent.Pre>())
            dup
            fload(1)
            invokespecial(
                internalNameOf<RenderGameOverlayEvent.Pre>(),
                "<init>",
                "(F)V"
            )
            callEvent()
        })

        mn.instructions.insertBefore(mn.instructions.findLast { it.opcode == RETURN }, asm {
            new(internalNameOf<RenderGameOverlayEvent.Post>())
            dup
            fload(1)
            invokespecial(
                internalNameOf<RenderGameOverlayEvent.Post>(),
                "<init>",
                "(F)V"
            )
            callEvent()
        })
    }
}
