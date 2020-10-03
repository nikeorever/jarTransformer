package cn.nikeo.transformer.jar

import java.io.InputStream
import java.util.jar.JarEntry

/**
 *
 */
interface JarEntryTransformer {
    /**
     *
     */
    fun transform(inputJarEntry: JarEntry, outputJarEntryInputStream: InputStream): ByteArray
}