package ravenweave.client.hook.event

import net.weavemc.api.Hook
import net.weavemc.api.event.CancellableEvent
import net.weavemc.internals.asm
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LabelNode
import ravenweave.client.callEvent
import ravenweave.client.event.GuiOpenEvent
import ravenweave.client.internalNameOf
import ravenweave.client.named

internal class GuiOpenEventHook : Hook("net/minecraft/client/Minecraft") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.named("displayGuiScreen").instructions.insert(asm {
            new(internalNameOf<GuiOpenEvent>())
            dup
            dup
            aload(1)
            invokespecial(
                internalNameOf<GuiOpenEvent>(),
                "<init>",
                "(Lnet/minecraft/client/gui/GuiScreen;)V"
            )
            callEvent()

            val end = LabelNode()

            invokevirtual(internalNameOf<CancellableEvent>(), "isCancelled", "()Z")
            ifeq(end)

            _return

            +end
            f_same()
        })
    }
}
