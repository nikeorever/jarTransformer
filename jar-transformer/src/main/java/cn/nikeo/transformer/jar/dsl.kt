package cn.nikeo.transformer.jar

import java.io.File
import java.io.InputStream

/**
 * Similar to [transformJar], except that you can use DSL to simplify the jar transform process.
 *
 * Sample:
 * ```kotlin
 * transformJarDsl(File("input.jar"), File("output.jar")){
 *      handleClassEntry { outputJarEntryInputStream ->
 *          // handle class file
 *          val clazz = ClassPool().makeClass(outputJarEntryInputStream)
 *          // modify bytecode
 *          if (clazz.name == "androidx.activity.R") {
 *              clazz.addField(CtField(CtClass.intType, "generated_int_field", clazz), "100")
 *          }
 *          clazz.toBytecode()
 *      }
 * }
 * ```
 */
fun transformJarDsl(jarInput: File, jarOutput: File, dsl: JarEntryHandlerDsl.() -> Unit) {
    JarEntryHandlerDslImpl().apply(dsl).transformJar(jarInput, jarOutput)
}

typealias JarEntryHandler = (InputStream) -> ByteArray

interface JarEntryHandlerDsl {
    /**
     *  Handle if the jar entry is a class file.
     */
    fun handleClassEntry(handler: JarEntryHandler)

    /**
     * Handle if the jar entry is a directory.
     */
    fun handleDirectoryEntry(handler: JarEntryHandler)

    /**
     * Handle if the jar entry is a normal file.
     */
    fun handleFileEntry(handler: JarEntryHandler)
}

private class JarEntryHandlerDslImpl : JarEntryHandlerDsl {
    private var classEntryHandler: JarEntryHandler? = null
    private var directoryEntryHandler: JarEntryHandler? = null
    private var fileEntryHandler: JarEntryHandler? = null

    override fun handleClassEntry(handler: JarEntryHandler) {
        classEntryHandler = handler
    }

    override fun handleDirectoryEntry(handler: JarEntryHandler) {
        directoryEntryHandler = handler
    }

    override fun handleFileEntry(handler: JarEntryHandler) {
        fileEntryHandler = handler
    }

    fun transformJar(jarInput: File, jarOutput: File) {
        transformJar(jarInput, jarOutput) { inputJarEntry, outputJarEntryInputStream ->
            when {
                inputJarEntry.isClassFile() -> {
                    classEntryHandler?.invoke(outputJarEntryInputStream) ?: outputJarEntryInputStream.readBytes()
                }
                inputJarEntry.isDirectory -> {
                    directoryEntryHandler?.invoke(outputJarEntryInputStream) ?: outputJarEntryInputStream.readBytes()
                }
                else -> {
                    fileEntryHandler?.invoke(outputJarEntryInputStream) ?: outputJarEntryInputStream.readBytes()
                }
            }
        }
    }
}

