@file:JvmName("JarTransformer")

package cn.nikeo.transformer.jar

import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * [jarInput]
 * [jarOutput]
 * [transformer]
 */
fun transformJar(jarInput: File, jarOutput: File, transformer: JarEntryTransformer) {
    val jarOutputStream = JarOutputStream(jarOutput.outputStream())

    val jarFileInput = JarFile(jarInput)
    val entries = jarFileInput.entries()
    while (entries.hasMoreElements()) {
        val inputJarEntry = entries.nextElement()

        val outputJarEntry = JarEntry(inputJarEntry.name)
        val outputJarEntryInputStream = jarFileInput.getInputStream(outputJarEntry)

        val outputByteArray = transformer.transform(inputJarEntry, outputJarEntryInputStream)

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
fun JarEntry.isClassFile() = !this.isDirectory && this.name.endsWith(".class")
