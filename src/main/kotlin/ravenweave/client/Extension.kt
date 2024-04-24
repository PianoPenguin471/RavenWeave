package ravenweave.client

import net.weavemc.api.event.Event
import net.weavemc.api.event.EventBus
import net.weavemc.internals.InsnBuilder
import net.weavemc.internals.asm
import org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.util.function.Predicate

internal fun List<MethodNode>.named(name: String) = find { it.name == name } ?: error("Method $name not found")

internal inline fun <reified T : Any> internalNameOf(): String = Type.getInternalName(T::class.java)

internal inline fun <reified T : AbstractInsnNode> AbstractInsnNode.next(p: (T) -> Boolean = { true }): T? =
    generateSequence(next) { it.next }.filterIsInstance<T>().find(p)

internal inline fun <reified T : Any> InsnBuilder.getSingleton() =
    getstatic(internalNameOf<T>(), "INSTANCE", "L${internalNameOf<T>()};")

internal fun InsnBuilder.callEvent() =
    invokestatic(internalNameOf<EventBus>(), "postEvent", "(L${internalNameOf<Event>()};)V")

internal fun Int.isStatic() = this and ACC_STATIC != 0
internal fun Int.isPublic() = this and ACC_PUBLIC != 0

internal fun ClassNode.generateForwarderMethod(vararg methods: String, prefix: String = "forwarder\$")  =
    generateForwarderMethod({ it.name in methods }, prefix)

internal fun ClassNode.generateForwarderMethod(methodsPredicate: Predicate<MethodNode>, prefix: String = "forwarder\$") {
    val methods = methods.filter { methodsPredicate.test(it) }
    if (methods.isEmpty()) return

    for (method in methods) {
        val newMethod = MethodNode(ACC_PUBLIC, prefix + method.name, method.desc, null, null)
        newMethod.instructions = asm {
            aload(0)

            var c = 0 // parse index
            val list = mutableListOf<String>()
            val desc = method.desc.substring(1).substringBefore(')')
            while (c < desc.length) {
                val ch = desc[c]
                if (ch == 'L') {
                    val end = desc.indexOf(';', c)
                    list.add(desc.substring(c, end + 1))
                    c = end + 1
                } else {
                    list.add(ch.toString())
                    c++
                }
            }

            list.fold(1) { index, type ->
                when (type) {
                    "B", "C", "I", "S", "Z" -> {
                        iload(index)
                        index + 1
                    }
                    "F" -> {
                        fload(index)
                        index + 1
                    }
                    "J" -> {
                        lload(index)
                        index + 2
                    }
                    "D" -> {
                        dload(index)
                        index + 2
                    }
                    else -> {
                        aload(index)
                        index + 1
                    }
                }
            }

            if (method.access.isPublic()) {
                invokevirtual(name, method.name, method.desc)
            } else {
                invokespecial(name, method.name, method.desc)
            }

            when (method.desc.last()) {
                'V' -> _return
                'B', 'C', 'I', 'S', 'Z' -> ireturn
                'F' -> freturn
                'J' -> lreturn
                'D' -> dreturn
                else -> areturn
            }
        }

        this.methods.add(newMethod)
    }
}
