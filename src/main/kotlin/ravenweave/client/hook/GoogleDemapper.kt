package ravenweave.client.hook

import net.weavemc.api.Hook
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode

/**
 * Remaps `net.weavemc.relocate.google.*` back to `com.google.*`.
 */
class GoogleDemapper : Hook("ravenweave/client/utils/Utils", "me/pianopenguin471/mixins/EntityRendererMixinHelper", "me/pianopenguin471/mixins/DummyPredicate") {
    private val realPackage = "com/google/"
    private val relocatedPackage = "net/weavemc/relocate/google/"

    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        println("GoogleDemapper transforming ${node.name}")

        if (node.superName.startsWith(relocatedPackage)) {
            node.superName = node.superName.replace(relocatedPackage, realPackage)
        }

        if (node.interfaces.isNotEmpty()) {
            for (i in 0 until node.interfaces.size) {
                if (node.interfaces[i].startsWith(relocatedPackage)) {
                    node.interfaces[i] = node.interfaces[i].replace(relocatedPackage, realPackage)
                }
            }
        }

        val instructions = node.methods.flatMap { it.instructions }
        for (instruction in instructions) {
            when (instruction) {
                is MethodInsnNode -> {
                    if (instruction.owner.startsWith(relocatedPackage)) {
                        instruction.owner = instruction.owner.replace(relocatedPackage, realPackage)
                    }

                    if (instruction.desc.contains(relocatedPackage)) {
                        instruction.desc = instruction.desc.replace(relocatedPackage, realPackage)
                    }
                }

                is FieldInsnNode -> {
                    if (instruction.owner.startsWith(relocatedPackage)) {
                        instruction.owner = instruction.owner.replace(relocatedPackage, realPackage)
                    }

                    if (instruction.desc.contains(relocatedPackage)) {
                        instruction.desc = instruction.desc.replace(relocatedPackage, realPackage)
                    }
                }

                is TypeInsnNode -> {
                    if (instruction.desc.contains(relocatedPackage)) {
                        instruction.desc = instruction.desc.replace(relocatedPackage, realPackage)
                    }
                }
            }
        }
    }
}