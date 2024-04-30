package ravenweave.client.hook

import me.pianopenguin471.mixins.EntityRendererMixinHelper
import net.weavemc.api.Hook
import net.weavemc.internals.asm
import org.objectweb.asm.tree.ClassNode
import ravenweave.client.internalNameOf
import ravenweave.client.named

class EntityRendererHook : Hook("net/minecraft/client/renderer/EntityRenderer") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        with(node.methods.named("updateLightmap")) {
            instructions = asm {
                aload(0)
                fload(1)
                invokestatic(
                    internalNameOf<EntityRendererMixinHelper>(),
                    EntityRendererMixinHelper::updateLightmap.name,
                    "(Ljava/lang/Object;F)V"
                )
                _return
            }

            localVariables.clear()
            tryCatchBlocks.clear()
        }

        cfg.computeFrames()
    }
}