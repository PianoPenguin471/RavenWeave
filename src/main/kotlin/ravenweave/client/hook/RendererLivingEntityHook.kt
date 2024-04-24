package ravenweave.client.hook

import net.weavemc.api.Hook
import net.weavemc.api.event.CancellableEvent
import net.weavemc.internals.asm
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LabelNode
import ravenweave.client.callEvent
import ravenweave.client.event.RenderLabelEvent
import ravenweave.client.internalNameOf
import ravenweave.client.named

class RendererLivingEntityHook : Hook("net/minecraft/client/renderer/entity/RendererLivingEntity") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        with(node.methods.named("renderName")) {
            instructions.insert(asm {
                new(internalNameOf<RenderLabelEvent>())
                dup
                dup
                aload(1)
                dload(2)
                dload(4)
                dload(6)
                invokespecial(
                    internalNameOf<RenderLabelEvent>(),
                    "<init>",
                    "(Lnet/minecraft/entity/Entity;DDD)V"
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
}