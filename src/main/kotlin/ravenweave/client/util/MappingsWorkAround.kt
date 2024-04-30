package ravenweave.client.util

import net.weavemc.api.Hook
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import ravenweave.client.internalNameOf
import java.lang.reflect.Method

/**
 * Serves as a workaround for the mappings issue.
 */
object MappingsWorkAround {
    val cache = mutableMapOf<String, Method>()

    @JvmStatic
    fun findMethod(ownerClass: Class<*>, id: String): Method {
        return cache.getOrPut(id) {
            val method = ownerClass.declaredMethods.find {
                it.getAnnotation(MappingsWorkAroundFinder.Marker::class.java)?.id == id
            } ?: error("Method with id $id not found in $ownerClass")

            method.isAccessible = true
            method
        }
    }
}

open class MappingsWorkAroundFinder(
    private val id: String,
    private val className: String,
    private val beforeMethodName: String,
    private val afterMethodName: String
) : Hook(className) {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        val methodWithIndex = node.methods.withIndex()
        val before = methodWithIndex.find { it.value.name == beforeMethodName }
            ?: error("Method $beforeMethodName not found in $className")
        val after = methodWithIndex.find { it.value.name == afterMethodName }
            ?: error("Method $afterMethodName not found in $className")

        require(after.index - before.index == 2)

        val method = node.methods[before.index + 1]
        with(method) {
            if (visibleAnnotations == null) {
                visibleAnnotations = mutableListOf()
            }

            visibleAnnotations.add(AnnotationNode("L${internalNameOf<Marker>()};").apply {
                values = listOf("id", id)
            })
        }
    }

    annotation class Marker(val id: String)
}