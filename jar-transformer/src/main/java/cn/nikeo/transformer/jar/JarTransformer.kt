@file:JvmName("JarTransformer")

package cn.nikeo.transformer.jar

import java.io.File
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 *  Reading the contents of jar file entry in the [jarInput] and applying the given [transform]
 *  function to it.
 *
 *  The [transform] function represents a transform function that accepts [JarEntry] and [InputStream]
 *  and produces a [ByteArray].
 *
 *  The [JarEntry] represents each jar file entry in the [jarInput]. The [InputStream] represents
 *  an input stream for reading the contents of the output jar file entry. The output jar file entry
 *  will be put into [jarOutput]. The [ByteArray] returned by the [transform] function will be written
 *  to [jarOutput].
 *
 *  @see transformJarDsl
 */
fun transformJar(jarInput: File, jarOutput: File, transform: (JarEntry, InputStream) -> ByteArray) {
    val jarOutputStream = JarOutputStream(jarOutput.outputStream())

    val jarFileInput = JarFile(jarInput)
    val entries = jarFileInput.entries()
    while (entries.hasMoreElements()) {
        val inputJarEntry = entries.nextElement()

        val outputJarEntry = JarEntry(inputJarEntry.name)
        val outputJarEntryInputStream = jarFileInput.getInputStream(outputJarEntry)

        val outputByteArray = transform(inputJarEntry, outputJarEntryInputStream)

        jarOutputStream.putNextEntry(outputJarEntry)
        jarOutputStream.write(outputByteArray)
        jarOutputStream.closeEntry()
    }

    jarOutputStream.close()
    jarFileInput.close()
}

/**
 * Checks if a Jar entry is a .class file.
 */
fun JarEntry.isClassFile() = !isDirectory && this.name.endsWith(".class")
